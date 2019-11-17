package com.ksisu.secret.service.impl

import cats.instances.try_._
import com.ksisu.secret.service.SecretService._
import org.scalatest.{Matchers, WordSpecLike}
import spray.json._

import scala.util.{Success, Try}

class SecretServiceImplSpec extends WordSpecLike with Matchers {
  trait TestScope {
    val nowInMillis: Long = 100L
    implicit lazy val redisServiceMock: RedisServiceMemoryImpl[Try] = new RedisServiceMemoryImpl[Try]() {
      override def nowMillis: Long = nowInMillis
    }
    lazy val service = new SecretServiceImpl[Try]()
  }

  "SecretServiceImpl" should {
    "#storeSecret" should {
      val data = CreateSecretData("SECRET", forgetAfter = 60, readOnlyOnce = false)
      "send to redis" in new TestScope {
        val key: Try[String] = service.storeSecret(data)
        key shouldEqual Success(redisServiceMock.getLastGeneratedKey)
      }
      "convert to json" in new TestScope {
        val key: String = service.storeSecret(data).get
        redisServiceMock.store(key)._2.parseJson shouldEqual data.toJson
      }
      "set timeout" in new TestScope {
        val key: String = service.storeSecret(data).get
        redisServiceMock.store(key)._1 shouldEqual (nowInMillis + data.forgetAfter * 1000)
      }
    }
    "#findSecret" should {
      "return none when not exists" in new TestScope {
        service.findSecret("ID").value shouldEqual Success(None)
      }
      "return none when wrong data in db" in new TestScope {
        redisServiceMock.store += "ID" -> ((1000, "{}"))
        service.findSecret("ID").value shouldEqual Success(None)
      }
      val data     = CreateSecretData("SECRET", forgetAfter = 60, readOnlyOnce = false)
      val showData = ShowSecretData("SECRET")
      "return the secret" in new TestScope {
        redisServiceMock.store += "ID" -> ((1000, data.toJson.compactPrint))
        service.findSecret("ID").value shouldEqual Success(Some(showData))
      }
      "find the secret multiple times" in new TestScope {
        redisServiceMock.store += "ID" -> ((1000, data.toJson.compactPrint))
        service.findSecret("ID").value shouldEqual Success(Some(showData))
        service.findSecret("ID").value shouldEqual Success(Some(showData))
      }
      "delete after fist time when readonlyonce " in new TestScope {
        redisServiceMock.store += "ID" -> ((1000, data.copy(readOnlyOnce = true).toJson.compactPrint))
        service.findSecret("ID").value shouldEqual Success(Some(showData))
        service.findSecret("ID").value shouldEqual Success(None)
      }
    }
  }
}
