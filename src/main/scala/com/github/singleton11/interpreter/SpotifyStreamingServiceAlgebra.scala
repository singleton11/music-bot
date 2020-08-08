package com.github.singleton11.interpreter

import cats.effect.{ConcurrentEffect, IO}
import com.github.singleton11.algebra.StreamingServiceAlgebra
import com.github.singleton11.domain.error.{NoArtistFoundError, NoItemsInResponseError, SomethingWentWrongWhenLike, SomethingWentWrongWhenSearch}
import com.github.singleton11.domain.{CurrentTrack, Track}
import com.github.singleton11.dto.spotify.Response
import org.http4s.Method._
import org.http4s._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global

object SpotifyStreamingServiceAlgebra {

  val HOST = uri"https://api.spotify.com"

  def IOInterpreter(implicit effect: ConcurrentEffect[IO], entityDecoder: EntityDecoder[IO, Response]): StreamingServiceAlgebra[IO] = new StreamingServiceAlgebra[IO] {
    override def search(authToken: String)(currentTrack: CurrentTrack): IO[Track] = {
      val uri = composeSearchUri(s"${currentTrack.artist} ${currentTrack.track}")
      val request = composeSearchRequest(authToken, uri)
      BlazeClientBuilder[IO](global).resource.use {
        client =>
          client.expect[Response](request)
            .handleErrorWith(throwable => IO.raiseError(SomethingWentWrongWhenSearch(throwable)))
            .flatMap(response => getValidatedTrack(response, currentTrack.artist))
      }
    }

    override def like(authToken: String)(serviceIdentifier: String): IO[Unit] = {
      val uri = composeLikeUri(serviceIdentifier)
      val request = composeLikeRequest(authToken, uri)
      BlazeClientBuilder[IO](global)(effect).resource.use { client =>
        client.status(request).flatMap(status => {
          if (status.code != 200) {
            IO.raiseError(SomethingWentWrongWhenLike())
          } else {
            IO.pure()
          }
        })
      }
    }

    //noinspection SameParameterValue
    private def composeSearchUri(query: String) = HOST
      .withPath("/v1/search")
      .withQueryParam("q", query)
      .withQueryParam("type", "track")

    private def composeSearchRequest(authToken: String, uri: Uri) = GET(
      uri,
      Authorization(Credentials.Token(AuthScheme.Bearer, authToken)),
      Accept(MediaType.application.json)
    )

    private def composeLikeUri(serviceIdentifier: String) = HOST
      .withPath("/v1/me/tracks")
      .withQueryParam("ids", serviceIdentifier)

    private def composeLikeRequest(authToken: String, uri: Uri) = PUT(
      uri,
      Authorization(Credentials.Token(AuthScheme.Bearer, authToken)),
      Accept(MediaType.application.json)
    )
  }

  private def getValidatedTrack(response: Response, artist: String): IO[Track] = {
    val errorOrTrack = for {
      track <- response.tracks.items.headOption.toRight(NoItemsInResponseError())
      artist <- track.artists.find(value => value.name == artist).toRight(NoArtistFoundError())
    } yield Track(track.id, track.name, artist.name)
    IO.fromEither(errorOrTrack)
  }
}
