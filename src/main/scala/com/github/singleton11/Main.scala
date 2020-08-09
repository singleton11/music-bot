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
      coreRadioCurrentTrackAlgebraInterpreter = CoreRadioCurrentTrackAlgebra.IOInterpreter
      currentTrack <- coreRadioCurrentTrackAlgebraInterpreter.getCurrentTrack
      _ <- logger.info(s"Current track is $currentTrack")
      systemEnvEnvironmentAlgebraInterpreter = SystemEnvEnvironmentAlgebra.IOInterpreter
      authToken <- systemEnvEnvironmentAlgebraInterpreter.getSpotifyAuthorizationToken
      spotifyStreamingServiceAlgebraInterpreter = SpotifyStreamingServiceAlgebra.IOInterpreter(authToken)
      _ <- logger.info(s"Auth token is $authToken")
      track <- spotifyStreamingServiceAlgebraInterpreter.search(currentTrack)
      _ <- logger.info(s"Current track found in spotify $track")
      _ <- spotifyStreamingServiceAlgebraInterpreter.like(track.serviceIdentifier)
      _ <- logger.info("Track liked")
    } yield ExitCode.Success
  }
}
