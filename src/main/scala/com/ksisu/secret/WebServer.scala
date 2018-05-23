package com.ksisu.secret

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object WebServer {

  def main(args: Array[String]) {
    implicit val system = ActorSystem("tell-me-a-secret-system")
    implicit val materializer = ActorMaterializer()

    val serviceService = system.actorOf(SecretService.props())
    val secretApi = new SecretApi(serviceService)

    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
    Http().bindAndHandle(cors() { secretApi.route }, "0.0.0.0", 8080)
  }
}