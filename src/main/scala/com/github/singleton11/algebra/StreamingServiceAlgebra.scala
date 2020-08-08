package com.github.singleton11.algebra

import com.github.singleton11.domain.{CurrentTrack, Track}

trait StreamingServiceAlgebra[F[_]] {
  def search(authToken: String)(currentTrack: CurrentTrack): F[Track]

  def like(authToken: String)(serviceIdentifier: String): F[Unit]
}
