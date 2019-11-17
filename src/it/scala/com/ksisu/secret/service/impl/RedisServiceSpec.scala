package com.ksisu.secret.service.impl

import com.ksisu.secret.service.RedisService
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class RedisServiceSpec extends WordSpecLike with Matchers {
  import scala.concurrent.ExecutionContext.Implicits.global
  import cats.instances.future._

  def await[T](f: Future[T]): T = Await.result(f, 5.seconds)

  private val config = ConfigFactory.load()
  implicit val redisConf: RedisServiceImpl.Config =
    RedisServiceImpl.Config(config.getString("redis.host"), config.getInt("redis.port"))

  val serivces: Seq[(String, RedisService[Future])] = Seq(
    "memory" -> new RedisServiceMemoryImpl[Future](),
    "real"   -> new RedisServiceImpl()
  )

  val data: String = "SECRET_VALUE"

  serivces.foreach {
    case (name, service) =>
      s"RedisService - $name" should {
        "store, find, remove secret" in {
          val key = await(service.store(data, 1.minute))
          await(service.find(key)) shouldEqual Some(data)
          await(service.remove(key))
          await(service.find(key)) shouldEqual None
        }
        "delete after time" in {
          val key = await(service.store(data, 1.second))
          await(service.find(key)) shouldEqual Some(data)
          Thread.sleep(1000)
          await(service.find(key)) shouldEqual None
        }
      }
  }
}
