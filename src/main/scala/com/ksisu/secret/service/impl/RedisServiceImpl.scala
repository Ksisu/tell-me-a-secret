package com.ksisu.secret.service.impl

import java.util.UUID

import com.ksisu.secret.service.RedisService
import com.ksisu.secret.service.impl.RedisServiceImpl.Config
import com.redis.{RedisClient, Seconds}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

class RedisServiceImpl()(implicit config: Config, ec: ExecutionContext) extends RedisService[Future] {
  private lazy val client = new RedisClient(config.host, config.port)

  override def store(value: String, time: FiniteDuration): Future[String] = {
    Future {
      val key = UUID.randomUUID().toString
      client.set(key, value, onlyIfExists = false, Seconds(time.toSeconds))
      key
    }
  }

  override def find(key: String): Future[Option[String]] = {
    Future {
      client.get(key)
    }
  }

  override def remove(key: String): Future[Unit] = {
    Future {
      client.del(key)
    }
  }
}

object RedisServiceImpl {
  case class Config(host: String, port: Int)
}
