package com.ksisu.secret.service.impl

import java.util.UUID

import cats.MonadError
import com.ksisu.secret.BuildInfo
import com.ksisu.secret.service.{HealthCheckService, RedisService}
import com.ksisu.secret.service.HealthCheckService.HealthCheckResult

import scala.concurrent.duration._

class HealthCheckServiceImpl[F[_]](implicit redisService: RedisService[F], me: MonadError[F, Throwable])
    extends HealthCheckService[F] {
  import cats.syntax.applicative._
  import cats.syntax.functor._
  import cats.syntax.flatMap._
  import cats.syntax.applicativeError._

  def getStatus: F[HealthCheckResult] = {
    val redisStatusF = (for {
      data   <- UUID.randomUUID.toString.pure
      id     <- redisService.store(data, 1.minute)
      result <- redisService.find(id)
    } yield result.contains(data)).recover { case _ => false }

    redisStatusF.map { redisStatus =>
      HealthCheckResult(
        success = redisStatus,
        BuildInfo.version,
        BuildInfo.builtAtString,
        BuildInfo.builtAtMillis,
        BuildInfo.commitHash
      )
    }
  }
}
