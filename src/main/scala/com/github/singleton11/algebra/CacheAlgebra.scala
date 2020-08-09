package com.github.singleton11.algebra

trait CacheAlgebra[F[_]] {

  def putAuthToken(authToken: String): F[Unit]

  def getAuthToken: F[String]

}
