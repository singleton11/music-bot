package com.github.singleton11.algebra

import cats.tagless.autoFunctorK

@autoFunctorK
trait EnvironmentAlgebra[F[_]] {
  def getRedisHost: F[Option[String]]

  def getSpotifyAuthorizationToken: F[Option[String]]
}
