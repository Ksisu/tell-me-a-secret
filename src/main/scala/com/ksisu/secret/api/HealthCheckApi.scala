package com.ksisu.secret.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ksisu.secret.Api
import com.ksisu.secret.service.HealthCheckService

import scala.concurrent.Future

class HealthCheckApi()(implicit service: HealthCheckService[Future]) extends Api {
  def route(): Route = {
    path("healthCheck") {
      get {
        onSuccess(service.getStatus) { result =>
          complete(result)
        }
      }
    }
  }
}
