package com.github.singleton11.algebra

import com.github.singleton11.domain.{CurrentTrack, Track}

trait StreamingServiceAlgebra[F[_]] {
  def search(currentTrack: CurrentTrack): F[Track]

  def like(serviceIdentifier: String): F[Unit]
}
