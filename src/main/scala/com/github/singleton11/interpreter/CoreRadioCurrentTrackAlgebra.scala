package com.github.singleton11.interpreter


import cats.effect.{ConcurrentEffect, IO}
import com.github.singleton11.algebra.CurrentTrackAlgebra
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global


object CoreRadioCurrentTrackAlgebra {

  private val URI = uri"https://coreradio.ru/icecast2_dark.php"

  def IOInterpreter(effect: ConcurrentEffect[IO]): CurrentTrackAlgebra[IO, String] = new CurrentTrackAlgebra[IO, String] {
    override def getCurrentTrack: IO[String] = getCurrentTrackFromCoreRadioHttpCall(effect)
  }

  private def getCurrentTrackFromCoreRadioHttpCall(implicit effect: ConcurrentEffect[IO]): IO[String] = {
    BlazeClientBuilder[IO](global).resource.use { client => client.expect[String](URI) }
  }
}
