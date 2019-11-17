package com.ksisu.secret.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.ksisu.secret.Api
import com.ksisu.secret.service.SecretService
import com.ksisu.secret.service.SecretService.CreateSecretData
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._

class SecretApi()(implicit serviceService: SecretService[Future]) extends Api {
  implicit val timeout: Timeout = Timeout(5.seconds)

  def route(): Route =
    pathPrefix("api" / "secret") {
      post {
        entity(as[CreateSecretData]) { secret =>
          onSuccess(serviceService.storeSecret(secret)) { uuid =>
            complete(HttpEntity(ContentTypes.`application/json`, s"""{"uuid":"$uuid"}"""))
          }
        }
      } ~
        path(JavaUUID) { uuid =>
          get {
            onSuccess(serviceService.findSecret(uuid.toString).value) {
              case Some(secret) => complete(secret.toJson)
              case _            => complete(HttpResponse(NotFound))
            }
          }
        }
    } ~ {
      complete(HttpResponse(NotFound))
    }
}
