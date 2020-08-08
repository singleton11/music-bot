package com.github.singleton11.interpreter


import cats.effect.{ConcurrentEffect, IO}
import com.github.singleton11.algebra.CurrentTrackAlgebra
import com.github.singleton11.domain.CurrentTrack
import com.github.singleton11.domain.error.HtmlCurrentTrackParsingError
import com.github.singleton11.util.implicits.StringUtils
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global


object CoreRadioCurrentTrackAlgebra {

  private val URI = uri"https://coreradio.ru/icecast2_dark.php"

  def IOInterpreter(implicit effect: ConcurrentEffect[IO]): CurrentTrackAlgebra[IO] = new CurrentTrackAlgebra[IO] {
    override def getCurrentTrack: IO[CurrentTrack] = for {
      html <- getCurrentTrackFromCoreRadioHttpCall
      currentTrack <- getCurrentTrackFromHtml(html)
    } yield currentTrack
  }

  private def getCurrentTrackFromCoreRadioHttpCall(implicit effect: ConcurrentEffect[IO]): IO[String] =
    BlazeClientBuilder[IO](global).resource.use { client => client.expect[String](URI) }


  private def getCurrentTrackFromHtml(html: String): IO[CurrentTrack] = {
    val maybeTrack = for {
      rawTrackValue <- getRawTrackValue(html)
      currentTrack <- getCurrentTrack(rawTrackValue)
    } yield currentTrack
    maybeTrack match {
      case Some(currentTrack) => IO.pure(currentTrack)
      case None => IO.raiseError(HtmlCurrentTrackParsingError())
    }
  }

  private def getRawTrackValue(html: String) = for {
    start <- html indexAfter "<title>"
    end <- html indexBefore "</title>"
  } yield html.slice(start, end)

  private def getCurrentTrack(rawTrackValue: String) = for {
    track <- (rawTrackValue indexBefore " - ").map(value => rawTrackValue.slice(0, value))
    artist <- (rawTrackValue indexAfter " - ").map(value => rawTrackValue.slice(value, rawTrackValue.length))
  } yield CurrentTrack(track, artist)
}
