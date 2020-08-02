package com.github.singleton11

import cats.data.{EitherT, OptionT}
import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}
import com.github.singleton11.domain.error.NoAuthTokenDefined
import com.github.singleton11.dto.spotify.Response
import com.github.singleton11.interpreter.{CoreRadioCurrentTrackAlgebra, SpotifyStreamingServiceAlgebra, SystemEnvEnvironmentAlgebra}
import com.github.singleton11.repository.{CurrentTrackRepository, StreamingServiceRepository}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.generic.auto._
import org.http4s.circe._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val result = (for {
      logger <- EitherT.liftF(OptionT.liftF(Slf4jLogger.create[IO]))
      currentTrack <- EitherT.liftF(OptionT(currentTrackRepository(logger).get))
      track <- EitherT(OptionT.liftF(streamingServiceRepository(logger).searchOne(currentTrack)))
      _ <- EitherT(OptionT.liftF(streamingServiceRepository(logger).like(track.serviceIdentifier)))
    } yield ExitCode.Success).value.value

    result.map(f => f.getOrElse(Left(NoAuthTokenDefined())).getOrElse(ExitCode.Error))
  }

  private def currentTrackRepository(logger: Logger[IO]) = {
    new CurrentTrackRepository[IO](CoreRadioCurrentTrackAlgebra.IOInterpreter(implicitly[ConcurrentEffect[IO]]), logger)
  }

  private def streamingServiceRepository(logger: Logger[IO]) = {
    new StreamingServiceRepository[IO](SpotifyStreamingServiceAlgebra.IOInterpreter(implicitly[ConcurrentEffect[IO]], jsonOf[IO, Response]), SystemEnvEnvironmentAlgebra.IOInterpreter, logger)
  }
}
