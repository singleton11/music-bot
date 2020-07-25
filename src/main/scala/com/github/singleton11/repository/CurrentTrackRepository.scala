package com.github.singleton11.repository


import cats.Monad
import com.github.singleton11.algebra.CurrentTrackAlgebra
import com.github.singleton11.domain.CurrentTrack
import com.github.singleton11.util.implicits._

class CurrentTrackRepository[F[_]](algebra: CurrentTrackAlgebra[F, String])(implicit m: Monad[F]) {
  def get: F[Option[CurrentTrack]] = m.map(algebra.getCurrentTrack)(value => getCurrentTrackFromHtml(value))

  def getRawTrackValue(html: String): Option[String] = {
    for {
      start <- html indexAfter "<title>"
      end <- html indexBefore "</title>"
    } yield html.slice(start, end)
  }

  def getCurrentTrack(rawTrackValue: String): Option[CurrentTrack] = {
    for {
      track <- (rawTrackValue indexBefore " - ").map(value => rawTrackValue.slice(0, value))
      artist <- (rawTrackValue indexAfter " - ").map(value => rawTrackValue.slice(value, rawTrackValue.length))
    } yield CurrentTrack(track, artist)
  }

  def getCurrentTrackFromHtml(html: String): Option[CurrentTrack] = {
    for {
      rawTrackValue <- getRawTrackValue(html)
      currentTrack <- getCurrentTrack(rawTrackValue)
    } yield currentTrack
  }
}
