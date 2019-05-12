package com.ksisu.secret.service.impl

import com.typesafe.config.{Config, ConfigFactory}

class ConfigService {
  private lazy val config: Config = ConfigFactory.load

  implicit lazy val redisConfig: RedisServiceImpl.Config = {
    val c = config.atKey("database")
    RedisServiceImpl.Config(
      c.getString("host"),
      c.getInt("port")
    )
  }
}
