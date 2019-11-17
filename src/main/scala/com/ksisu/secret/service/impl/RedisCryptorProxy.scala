package com.ksisu.secret.service.impl

import java.util.concurrent.Executors

import com.ksisu.secret.service.RedisService
import com.ksisu.secret.util.Cryptor

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

class RedisCryptorProxy(redisService: RedisService[Future])(implicit conf: RedisCryptorProxy.Config)
    extends RedisService[Future] {
  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(conf.threads))

  override def store(value: String, time: FiniteDuration): Future[String] = {
    Future {
      Cryptor.encrypt(value, secret)
    }.flatMap { data =>
      redisService.store(data, time)
    }
  }

  override def find(key: String): Future[Option[String]] = {
    redisService
      .find(key)
      .flatMap(_.map { data =>
        Future {
          Some(Cryptor.decrypt(data, secret))
        }
      }.getOrElse(Future.successful(None)))
  }

  override def remove(key: String): Future[Unit] = redisService.remove(key)

  private def secret: Array[Byte] = conf.secret.getBytes
}

object RedisCryptorProxy {
  case class Config(secret: String, threads: Int)
}
