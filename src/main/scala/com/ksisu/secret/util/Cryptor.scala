package com.ksisu.secret.util

import java.security.SecureRandom
import java.util.Base64

import javax.crypto.spec.{GCMParameterSpec, SecretKeySpec}
import javax.crypto.{Cipher, KeyGenerator, SecretKey}

// https://gist.github.com/Ksisu/da36ead2a76e150220a531a5296bf603
object Cryptor {
  private val AES_KEY_SIZE               = 256
  private val AES_KEY_SIZE_IN_BYTE       = AES_KEY_SIZE / 8
  private val IV_SIZE                    = 128
  private val TAG_BIT_LENGTH             = 128
  private val ALGO_TRANSFORMATION_STRING = "AES/GCM/PKCS5Padding"
  private val ALGO                       = "AES"

  private val secureRandom = new SecureRandom()

  def encrypt(message: String, secret: Array[Byte]): String = {
    val aesKey: SecretKey = generateAesKey()
    val gcmParameterSpec  = generateGcmParameterSpec()

    val cipher = Cipher.getInstance(ALGO_TRANSFORMATION_STRING)
    cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmParameterSpec, new SecureRandom())
    cipher.updateAAD(secret)

    val encryptedMessage = cipher.doFinal(message.getBytes)
    encodeData(aesKey, gcmParameterSpec, encryptedMessage)
  }

  def decrypt(encryptedDataString: String, secret: Array[Byte]): String = {
    val (aesKey, gcmParameterSpec, encryptedMessage) = decodeData(encryptedDataString)

    val cipher = Cipher.getInstance(ALGO_TRANSFORMATION_STRING)
    cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmParameterSpec, new SecureRandom())
    cipher.updateAAD(secret)

    val message = cipher.doFinal(encryptedMessage)
    new String(message)
  }

  private def generateAesKey(): SecretKey = {
    val keygen = KeyGenerator.getInstance(ALGO)
    keygen.init(AES_KEY_SIZE)
    keygen.generateKey
  }

  private def generateGcmParameterSpec(): GCMParameterSpec = {
    val iv = new Array[Byte](IV_SIZE)
    secureRandom.nextBytes(iv)
    new GCMParameterSpec(TAG_BIT_LENGTH, iv)
  }

  private def encodeData(
      aesKey: SecretKey,
      gcmParameterSpec: GCMParameterSpec,
      encryptedMessage: Array[Byte]
  ): String = {
    val data = aesKey.getEncoded ++ gcmParameterSpec.getIV ++ encryptedMessage
    Base64.getEncoder.encodeToString(data)
  }

  private def decodeData(encodedData: String): (SecretKeySpec, GCMParameterSpec, Array[Byte]) = {
    val data             = Base64.getDecoder.decode(encodedData)
    val aesKey           = new SecretKeySpec(data.take(AES_KEY_SIZE_IN_BYTE), ALGO)
    val iv               = data.slice(AES_KEY_SIZE_IN_BYTE, AES_KEY_SIZE_IN_BYTE + IV_SIZE)
    val gcmParameterSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv)
    val encryptedMessage = data.drop(AES_KEY_SIZE_IN_BYTE + IV_SIZE)
    (aesKey, gcmParameterSpec, encryptedMessage)
  }
}
