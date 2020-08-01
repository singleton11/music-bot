package com.github.singleton11.interpreter

import cats.effect.IO
import cats.~>
import com.github.singleton11.algebra.EnvironmentAlgebra
import cats.tagless.implicits._
import cats.implicits._
import cats._


object SystemEnvEnvironmentAlgebra {

  implicit val fk: Option ~> IO = λ[Option ~> IO] {
    case Some(value) => IO(value)
    case None => IO.never
  }

  val OptionInterpreter: EnvironmentAlgebra[Option] = new EnvironmentAlgebra[Option] {
    override def getRedisHost: Option[String] = sys.env.get("REDIS_HOST")

    override def getSpotifyAuthorizationToken: Option[String] = sys.env.get("SPOTIFY_AUTH_TOKEN")
  }

  val IOInterpreter: EnvironmentAlgebra[IO] = OptionInterpreter.mapK(fk)
}
