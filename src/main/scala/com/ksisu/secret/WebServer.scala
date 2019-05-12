package com.ksisu.secret

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.ksisu.secret.util.LoggerUtil
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object WebServer {

  def main(args: Array[String]) {
    LoggerUtil.initBridge()
    val logger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)

    implicit val system: ActorSystem             = ActorSystem("tell-me-a-secret")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import system.dispatcher

    val services = Services.getReal()
    val api      = Api.createApi(services)

    Http()
      .bindAndHandle(api, "0.0.0.0", 8080)
      .map(setupShutdownHook)
      .onComplete {
        case Success(_) => logger.info("TellMeASecret started")
        case Failure(ex) =>
          logger.error("TellMeASecret starting failed", ex)
          system.terminate()
      }

    def setupShutdownHook(server: Http.ServerBinding): Unit = {
      CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceUnbind, "http_shutdown") { () =>
        logger.info("TellMeASecret shutting down...")
        server.terminate(hardDeadline = 8.seconds).map(_ => Done)
      }
    }
  }
}
