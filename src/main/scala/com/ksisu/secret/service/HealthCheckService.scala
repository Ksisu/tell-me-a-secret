package com.ksisu.secret.service

import com.ksisu.secret.service.HealthCheckService.HealthCheckResult
import spray.json.RootJsonFormat

trait HealthCheckService[F[_]] {
  def getStatus: F[HealthCheckResult]
}

object HealthCheckService {
  import spray.json.DefaultJsonProtocol._
  final case class HealthCheckResult(
      success: Boolean,
      version: String,
      buildAtString: String,
      buildAtMillis: Long,
      commitHash: Option[String]
  )
  implicit val healthCheckResultFormat: RootJsonFormat[HealthCheckResult] = jsonFormat5(HealthCheckResult)
}
