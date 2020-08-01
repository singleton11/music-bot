package com.github.singleton11.repository

import cats.Monad
import cats.implicits._
import com.github.singleton11.algebra.{EnvironmentAlgebra, StreamingServiceAlgebra}
import com.github.singleton11.domain.error.{NoArtistFoundError, NoItemsInResponseError, StreamingServiceError}
import com.github.singleton11.domain.{CurrentTrack, Track}
import com.github.singleton11.dto.spotify.Response
import io.chrisdavenport.log4cats.Logger

class StreamingServiceRepository[F[_] : Monad](serviceAlgebra: StreamingServiceAlgebra[F], environmentAlgebra: EnvironmentAlgebra[F], logger: Logger[F]) {
  def searchOne(currentTrack: CurrentTrack): F[Either[StreamingServiceError, Track]] = for {
    authToken <- environmentAlgebra.getSpotifyAuthorizationToken
    _ <- logger.info(s"Obtained auth token: $authToken")
    response <- serviceAlgebra.search(authToken)(s"${currentTrack.artist} ${currentTrack.track}")
    _ <- logger.info(s"Response is $response")
    track <- getValidatedTrack(response, currentTrack.artist)
    _ <- logger.info(s"Validated track is $track")
  } yield track

  private def getValidatedTrack(response: Response, artist: String): F[Either[StreamingServiceError, Track]] = {
    val errorOrTrack = for {
      track <- response.tracks.items.headOption.toRight(NoItemsInResponseError())
      artist <- track.artists.find(value => value.name == artist).toRight(NoArtistFoundError())
    } yield Track(track.id, track.name, artist.name)
    errorOrTrack.pure[F]
  }

}
