package com.github.singleton11

import cats.effect.{ExitCode, IO, IOApp}
import com.github.singleton11.dto.spotify.Response
import com.github.singleton11.interpreter.{CoreRadioCurrentTrackAlgebra, SpotifyStreamingServiceAlgebra, SystemEnvEnvironmentAlgebra}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.generic.auto._
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object Main extends IOApp {
  implicit val json: EntityDecoder[IO, Response] = jsonOf[IO, Response]

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      logger <- Slf4jLogger.create[IO]
      currentTrack <- CoreRadioCurrentTrackAlgebra.IOInterpreter.getCurrentTrack
      _ <- logger.info(s"Current track is $currentTrack")
      authToken <- SystemEnvEnvironmentAlgebra.IOInterpreter.getSpotifyAuthorizationToken
      _ <- logger.info(s"Auth token is $authToken")
      track <- SpotifyStreamingServiceAlgebra.IOInterpreter.search(authToken)(currentTrack)
      _ <- logger.info(s"Current track found in spotify $track")
      _ <- SpotifyStreamingServiceAlgebra.IOInterpreter.like(authToken)(track.serviceIdentifier)
      _ <- logger.info("Track liked")
    } yield ExitCode.Success
  }
}
