package com.github.singleton11.algebra

import cats.tagless.autoFunctorK

@autoFunctorK
trait EnvironmentAlgebra[F[_]] {
  def getRedisHost: F[String]

  def getSpotifyAuthorizationToken: F[String]
}
