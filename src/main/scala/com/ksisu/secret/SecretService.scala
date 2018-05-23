package com.ksisu.secret

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.ksisu.secret.SecretService.{FindSecret, RemoveSecret, StoreSecret}
import com.redis._
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration.Duration
object SecretService {
  def props() = Props(new SecretService())

  case class StoreSecret(secret: CreateSecretData)
  case class FindSecret(uuid: UUID)
  private case class RemoveSecret(uuid: UUID)

}

class SecretService extends Actor {

  val redisClient = new RedisClient("localhost", 6379)

  import context.dispatcher

  override def receive: Receive = {
    case StoreSecret(secret) => storeSecret(secret) pipeTo sender()
    case FindSecret(uuid) => findSecret(uuid) pipeTo sender()
    case RemoveSecret(uuid) => removeSecret(uuid)
  }

  private def storeSecret(secret: CreateSecretData): Future[UUID] = {
    val uuid = UUID.randomUUID()
    redisClient.set(uuid, secret.toJson.compactPrint)

    context.system.scheduler.scheduleOnce(Duration(secret.forgetAfter, "min")) {
      self ! RemoveSecret(uuid)
    }

    Future.successful(uuid)
  }

  private def findSecret(uuid: UUID): Future[Option[ShowSecretData]] = {
    val result = redisClient.get(uuid).map(_.parseJson.convertTo[CreateSecretData])
    result.foreach { secret =>
      if (secret.readOnlyOnce) {
        removeSecret(uuid)
      }
    }
    Future.successful(result.map(secret => ShowSecretData(secret.secret)))
  }

  private def removeSecret(uuid: UUID): Unit = {
    redisClient.del(uuid)
  }
}
