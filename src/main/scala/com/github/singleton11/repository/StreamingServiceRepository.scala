package com.github.singleton11.repository

import cats.Monad
import cats.data.OptionT
import cats.implicits._
import com.github.singleton11.algebra.{EnvironmentAlgebra, StreamingServiceAlgebra}
import com.github.singleton11.domain.error.{NoArtistFoundError, NoAuthTokenDefined, NoItemsInResponseError, StreamingServiceError}
import com.github.singleton11.domain.{CurrentTrack, Track}
import com.github.singleton11.dto.spotify.Response
import io.chrisdavenport.log4cats.Logger

class StreamingServiceRepository[F[_] : Monad](serviceAlgebra: StreamingServiceAlgebra[F], environmentAlgebra: EnvironmentAlgebra[F], logger: Logger[F]) {
  def searchOne(currentTrack: CurrentTrack): F[Either[StreamingServiceError, Track]] = (for {
    authToken <- OptionT(environmentAlgebra.getSpotifyAuthorizationToken)
    _ <- OptionT.liftF(logger.info(s"Obtained auth token: $authToken"))
    response <- OptionT.liftF(serviceAlgebra.search(authToken)(s"${currentTrack.artist} ${currentTrack.track}"))
    _ <- OptionT.liftF(logger.info(s"Response is $response"))
    track <- OptionT.liftF(getValidatedTrack(response, currentTrack.artist))
    _ <- OptionT.liftF(logger.info(s"Validated track is $track"))
  } yield track).getOrElse(Left(NoAuthTokenDefined()))

  def like(serviceIdentifier: String): F[Either[StreamingServiceError, Unit]] = {
    (for {
      authToken <- OptionT(environmentAlgebra.getSpotifyAuthorizationToken)
      _ <- OptionT.liftF(serviceAlgebra.like(authToken)(serviceIdentifier))
    } yield ()).value.map {
      case Some(value) => Right(value)
      case None => Left(NoAuthTokenDefined())
    }
  }

  private def getValidatedTrack(response: Response, artist: String): F[Either[StreamingServiceError, Track]] = {
    val errorOrTrack = for {
      track <- response.tracks.items.headOption.toRight(NoItemsInResponseError())
      artist <- track.artists.find(value => value.name == artist).toRight(NoArtistFoundError())
    } yield Track(track.id, track.name, artist.name)
    errorOrTrack.pure[F]
  }

}
