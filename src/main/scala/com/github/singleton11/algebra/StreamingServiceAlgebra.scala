package com.github.singleton11.algebra

import com.github.singleton11.dto.spotify.Response
import org.http4s.Status

trait StreamingServiceAlgebra[F[_]] {
  def search(authToken: String)(query: String): F[Response]

  def like(authToken: String)(serviceIdentifier: String): F[Status]
}
