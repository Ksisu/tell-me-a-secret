package com.ksisu.secret.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ksisu.secret.Api
import com.ksisu.secret.service.HealthCheckService

import scala.concurrent.Future
import scala.util.{Failure, Success}

class HealthCheckApi()(implicit service: HealthCheckService[Future]) extends Api {
  def route(): Route = {
    path("healthCheck") {
      get {
        onComplete(service.getStatus) {
          case Success(result) if result.success => complete(result)
          case Success(result)                   => complete(StatusCodes.InternalServerError -> result)
          case Failure(_)                        => complete(StatusCodes.InternalServerError)
        }
      }
    }
  }
}
