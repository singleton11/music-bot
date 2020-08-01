package com.github.singleton11.interpreter

import cats.effect.{ConcurrentEffect, IO}
import com.github.singleton11.algebra.StreamingServiceAlgebra
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

  def IOInterpreter(effect: ConcurrentEffect[IO], entityDecoder: EntityDecoder[IO, Response]): StreamingServiceAlgebra[IO] = new StreamingServiceAlgebra[IO] {
    override def search(authToken: String)(query: String): IO[Response] = {
      val uri = composeSearchUri(query)
      val request = composeSearchRequest(authToken, uri)
      BlazeClientBuilder[IO](global)(effect).resource.use { client => client.expect[Response](request)(entityDecoder) }
    }

    override def like(authToken: String)(serviceIdentifier: String): IO[Status] = {
      val uri = composeLikeUri(serviceIdentifier)
      val request = composeLikeRequest(authToken, uri)
      BlazeClientBuilder[IO](global)(effect).resource.use { client => client.status(request) }
    }

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
}
