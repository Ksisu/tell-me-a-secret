package com.ksisu.secret

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.ksisu.secret.api.{HealthCheckApi, SecretApi}

import scala.concurrent.Future

trait Api {
  def route(): Route
}

object Api {
  val emptyRoute = Route(_.reject())

  def createRoute(api: Seq[Api]): Route = {
    api
      .map(_.route())
      .fold(Api.emptyRoute)(_ ~ _)
  }

  def createApi(services: Services[Future]): Route = {
    import services._

    val api = Seq(
      new HealthCheckApi(),
      new SecretApi()
    )

    cors() {
      createRoute(api)
    }
  }
}
