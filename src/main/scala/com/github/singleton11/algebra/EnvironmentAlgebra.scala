package com.github.singleton11.algebra

trait EnvironmentAlgebra[F[_], T] {
  def getRedisHost: F[T]
}
