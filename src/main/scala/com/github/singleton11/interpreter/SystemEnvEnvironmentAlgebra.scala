package com.github.singleton11.interpreter

import cats.effect.IO
import com.github.singleton11.algebra.EnvironmentAlgebra


object SystemEnvEnvironmentAlgebra {

  val IOInterpreter: EnvironmentAlgebra[IO] = new EnvironmentAlgebra[IO] {
    override def getRedisHost: IO[Option[String]] = IO(sys.env.get("REDIS_HOST"))

    override def getSpotifyAuthorizationToken: IO[Option[String]] = IO(sys.env.get("SPOTIFY_AUTH_TOKEN"))
  }
}
