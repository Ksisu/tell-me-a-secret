package com.ksisu.secret.service.impl

import com.typesafe.config.{Config, ConfigFactory}

class ConfigService {
  private lazy val config: Config = ConfigFactory.load

  implicit lazy val redisConfig: RedisServiceImpl.Config = {
    val c = config.getConfig("redis")
    RedisServiceImpl.Config(
      c.getString("host"),
      c.getInt("port")
    )
  }

  implicit lazy val cryptorConfig: RedisCryptorProxy.Config = {
    val c = config.getConfig("cryptor")
    RedisCryptorProxy.Config(
      c.getString("secret"),
      c.getInt("threads")
    )
  }
}
