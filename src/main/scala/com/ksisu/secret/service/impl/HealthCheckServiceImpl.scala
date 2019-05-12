package com.ksisu.secret.service.impl

import cats.Monad
import com.ksisu.secret.BuildInfo
import com.ksisu.secret.service.HealthCheckService
import com.ksisu.secret.service.HealthCheckService.HealthCheckResult

class HealthCheckServiceImpl[F[_]: Monad]() extends HealthCheckService[F] {
  import cats.syntax.applicative._

  def getStatus: F[HealthCheckResult] = {
    HealthCheckResult(
      success = true,
      BuildInfo.version,
      BuildInfo.builtAtString,
      BuildInfo.builtAtMillis,
      BuildInfo.commitHash
    ).pure
  }
}
