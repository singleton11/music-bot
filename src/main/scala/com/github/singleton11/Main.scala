package com.github.singleton11

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}
import com.github.singleton11.dto.spotify.Response
import com.github.singleton11.interpreter.{CoreRadioCurrentTrackAlgebra, SpotifyStreamingServiceAlgebra, SystemEnvEnvironmentAlgebra}
import com.github.singleton11.repository.{CurrentTrackRepository, StreamingServiceRepository}
import io.chrisdavenport.log4cats.Logger
import io.circe.generic.auto._
import org.http4s.circe._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    IO(ExitCode.Success)
  }

  private def currentTrackRepository(logger: Logger[IO]) = {
    new CurrentTrackRepository[IO](CoreRadioCurrentTrackAlgebra.IOInterpreter(implicitly[ConcurrentEffect[IO]]), logger)
  }

  private def streamingServiceRepository(logger: Logger[IO]) = {
    new StreamingServiceRepository[IO](SpotifyStreamingServiceAlgebra.IOInterpreter(implicitly[ConcurrentEffect[IO]], jsonOf[IO, Response]), SystemEnvEnvironmentAlgebra.IOInterpreter, logger)
  }
}
