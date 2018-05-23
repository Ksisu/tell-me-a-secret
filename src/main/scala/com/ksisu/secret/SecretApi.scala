package com.ksisu.secret

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.ksisu.secret.SecretService.{FindSecret, StoreSecret}

import scala.concurrent.duration._

class SecretApi(serviceService: ActorRef) {

  import SprayJsonSupport._
  import spray.json._

  implicit val timeout = Timeout(5.seconds)

  val route =
    pathPrefix("api" / "secret") {
      post {
        entity(as[CreateSecretData]) { secret =>
          onSuccess(serviceService ? StoreSecret(secret)) { uuid =>
            complete(HttpEntity(ContentTypes.`application/json`, s"""{"uuid":"$uuid"}"""))
          }
        }
      } ~
        path(JavaUUID) { uuid =>
          get {
            onSuccess((serviceService ? FindSecret(uuid)).mapTo[Option[ShowSecretData]]) {
              _.fold(
                complete(HttpResponse(NotFound))
              ) {
                secret => complete(HttpEntity(ContentTypes.`application/json`, secret.toJson.compactPrint))
              }
            }
          }
        }
    } ~ {
      complete(HttpResponse(NotFound))
    }

}
