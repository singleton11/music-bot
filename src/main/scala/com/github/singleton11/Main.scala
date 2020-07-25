package com.github.singleton11

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}
import com.github.singleton11.interpreter.CoreRadioCurrentTrackAlgebra
import com.github.singleton11.repository.CurrentTrackRepository

object Main extends IOApp {

  private val currentTrackRepository = new CurrentTrackRepository[IO](CoreRadioCurrentTrackAlgebra.IOInterpreter(implicitly[ConcurrentEffect[IO]]))

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      currentTrack <- currentTrackRepository.get
      _ <- IO(println(currentTrack))
    } yield ExitCode.Success
  }
}
