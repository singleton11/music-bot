package com.github.singleton11.repository


import cats.Monad
import cats.implicits._
import com.github.singleton11.algebra.CurrentTrackAlgebra
import com.github.singleton11.domain.CurrentTrack
import com.github.singleton11.util.implicits._

class CurrentTrackRepository[F[_] : Monad](algebra: CurrentTrackAlgebra[F, String]) {

  def get: F[Option[CurrentTrack]] = algebra.getCurrentTrack.map(value => getCurrentTrackFromHtml(value))

  private def getCurrentTrackFromHtml(html: String): Option[CurrentTrack] = {
    for {
      rawTrackValue <- getRawTrackValue(html)
      currentTrack <- getCurrentTrack(rawTrackValue)
    } yield currentTrack
  }

  private def getRawTrackValue(html: String): Option[String] = {
    for {
      start <- html indexAfter "<title>"
      end <- html indexBefore "</title>"
    } yield html.slice(start, end)
  }

  private def getCurrentTrack(rawTrackValue: String): Option[CurrentTrack] = {
    for {
      track <- (rawTrackValue indexBefore " - ").map(value => rawTrackValue.slice(0, value))
      artist <- (rawTrackValue indexAfter " - ").map(value => rawTrackValue.slice(value, rawTrackValue.length))
    } yield CurrentTrack(track, artist)
  }
}
