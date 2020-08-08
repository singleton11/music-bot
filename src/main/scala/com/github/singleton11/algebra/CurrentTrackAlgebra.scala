package com.github.singleton11.algebra

import com.github.singleton11.domain.CurrentTrack

trait CurrentTrackAlgebra[F[_]] {
  def getCurrentTrack: F[CurrentTrack]
}
