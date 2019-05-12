package com.ksisu.secret.service.impl

import cats.Monad
import com.ksisu.secret.service.RedisService

import scala.concurrent.duration.FiniteDuration

class RedisServiceMemoryImpl[F[_]: Monad]() extends RedisService[F] {
  import cats.syntax.applicative._

  var store: Map[String, (Long, String)] = Map.empty

  private var counter = 0

  override def store(value: String, time: FiniteDuration): F[String] = {
    counter += 1
    val id      = counter.toString
    val endTime = nowMillis + time.toMillis
    store += id -> ((endTime, value))
    id.pure
  }

  override def find(key: String): F[Option[String]] = {
    store
      .get(key)
      .collect {
        case (time, value) if time > nowMillis => value
      }
      .pure
  }

  override def remove(key: String): F[Unit] = {
    store -= key
    ().pure
  }

  def getLastGeneratedKey: String = counter.toString

  def nowMillis: Long = System.currentTimeMillis()
}
