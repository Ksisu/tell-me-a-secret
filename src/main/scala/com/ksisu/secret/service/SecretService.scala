package com.ksisu.secret.service

import cats.data.OptionT
import com.ksisu.secret.service.SecretService.{CreateSecretData, ShowSecretData}
import spray.json._

trait SecretService[F[_]] {
  def storeSecret(secret: CreateSecretData): F[String]
  def findSecret(id: String): OptionT[F, ShowSecretData]
}

object SecretService {
  case class CreateSecretData(secret: String, forgetAfter: Int, readOnlyOnce: Boolean)
  case class ShowSecretData(secret: String)

  import DefaultJsonProtocol._
  implicit val createSecretFormats: RootJsonFormat[CreateSecretData] = jsonFormat3(CreateSecretData.apply)
  implicit val showSecretFormats: RootJsonFormat[ShowSecretData]     = jsonFormat1(ShowSecretData.apply)
}
