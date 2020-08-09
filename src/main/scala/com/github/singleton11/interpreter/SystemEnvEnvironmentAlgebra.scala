package com.github.singleton11.interpreter

import cats.effect.IO
import com.github.singleton11.algebra.EnvironmentAlgebra
import com.github.singleton11.domain.SpotifyAuthTokenNotFound


object SystemEnvEnvironmentAlgebra {

  val IOInterpreter: EnvironmentAlgebra[IO] = new EnvironmentAlgebra[IO] {

    override def getSpotifyAuthorizationToken: IO[String] = {
      val maybeSpotifyAuthToken = sys.env.get("SPOTIFY_AUTH_TOKEN")
      maybeSpotifyAuthToken match {
        case Some(spotifyAuthToken) => IO.pure(spotifyAuthToken)
        case None => IO.raiseError(SpotifyAuthTokenNotFound())
      }
    }
  }
}
