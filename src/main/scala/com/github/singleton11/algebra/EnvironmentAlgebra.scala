package com.github.singleton11.algebra

trait EnvironmentAlgebra[F[_]] {
  def getSpotifyAuthorizationToken: F[String]
}
