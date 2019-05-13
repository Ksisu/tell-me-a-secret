package com.ksisu.secret

import com.ksisu.secret.service.impl._
import com.ksisu.secret.service.{HealthCheckService, RedisService, SecretService}

import scala.concurrent.{ExecutionContext, Future}

trait Services[F[_]] {
  implicit val healthCheckService: HealthCheckService[F]
  implicit val redisService: RedisService[F]
  implicit val secretService: SecretService[F]
}

object Services {
  def getReal()(implicit ec: ExecutionContext): Services[Future] = {
    val config = new ConfigService()
    new Services[Future] {
      import config._
      import cats.instances.future._

      override implicit val healthCheckService: HealthCheckService[Future] = new HealthCheckServiceImpl()
      override implicit val redisService: RedisService[Future]             = new RedisCryptorProxy(new RedisServiceImpl())
      override implicit val secretService: SecretService[Future]           = new SecretServiceImpl[Future]()
    }
  }
}
