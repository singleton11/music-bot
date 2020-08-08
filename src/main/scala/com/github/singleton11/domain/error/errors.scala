package com.github.singleton11.domain.error

case class NoItemsInResponseError() extends Throwable

case class NoArtistFoundError() extends Throwable

case class NoAuthTokenDefined() extends Throwable

case class HtmlCurrentTrackParsingError() extends Throwable

case class SpotifyAuthTokenNotFound() extends Throwable

case class SomethingWentWrongWhenLike() extends Throwable
case class SomethingWentWrongWhenSearch(throwable: Throwable) extends Throwable
