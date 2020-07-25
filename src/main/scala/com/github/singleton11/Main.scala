package com.github.singleton11

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}
import com.github.singleton11.interpreter.CoreRadioCurrentTrackAlgebra
import com.github.singleton11.repository.CurrentTrackRepository

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    new CurrentTrackRepository[IO](CoreRadioCurrentTrackAlgebra.IOInterpreter(implicitly[ConcurrentEffect[IO]])).get
      .map(value => println(value))
      .unsafeRunSync()

    IO.pure(ExitCode.Success)
  }
}
