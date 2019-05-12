package com.ksisu.secret.service.impl

import cats.Monad
import cats.data.OptionT
import com.ksisu.secret.service.SecretService.{CreateSecretData, ShowSecretData}
import com.ksisu.secret.service.{RedisService, SecretService}
import spray.json._

import scala.concurrent.duration._
import scala.util.Try

class SecretServiceImpl[F[_]: Monad](implicit redisService: RedisService[F]) extends SecretService[F] {
  override def storeSecret(secret: CreateSecretData): F[String] = {
    redisService.store(secret.toJson.compactPrint, secret.forgetAfter.seconds)
  }

  override def findSecret(id: String): OptionT[F, ShowSecretData] = {
    for {
      data   <- OptionT(redisService.find(id))
      secret <- OptionT.fromOption(Try(data.parseJson.convertTo[CreateSecretData]).toOption)
    } yield {
      if (secret.readOnlyOnce) {
        redisService.remove(id)
      }
      ShowSecretData(secret.secret)
    }
  }
}
