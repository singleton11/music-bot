package com.github.singleton11.algebra

trait CurrentTrackAlgebra[F[_], T] {
  def getCurrentTrack: F[T]
}
