package com.ksisu

import spray.json.DefaultJsonProtocol

package object secret {
  case class CreateSecretData(secret: String, forgetAfter: Int, readOnlyOnce: Boolean)
  case class ShowSecretData(secret: String)

  import DefaultJsonProtocol._
  implicit val createSecretFormats = jsonFormat3(CreateSecretData.apply)
  implicit val showSecretFormats = jsonFormat1(ShowSecretData.apply)
}
