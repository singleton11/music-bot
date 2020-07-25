package com.github.singleton11

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}
import com.github.singleton11.interpreter.CoreRadioCurrentTrackAlgebra
import com.github.singleton11.repository.CurrentTrackRepository
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      logger <- Slf4jLogger.create[IO]
      _ <- getCurrentTrack(logger)
    } yield ExitCode.Success
  }

  private def getCurrentTrack(logger: Logger[IO]) = {
    new CurrentTrackRepository[IO](CoreRadioCurrentTrackAlgebra.IOInterpreter(implicitly[ConcurrentEffect[IO]]), logger).get
  }
}
