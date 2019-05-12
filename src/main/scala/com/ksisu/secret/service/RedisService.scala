package com.ksisu.secret.service

import scala.concurrent.duration.FiniteDuration

trait RedisService[F[_]] {
  def store(value: String, time: FiniteDuration): F[String]
  def find(key: String): F[Option[String]]
  def remove(key: String): F[Unit]
}
