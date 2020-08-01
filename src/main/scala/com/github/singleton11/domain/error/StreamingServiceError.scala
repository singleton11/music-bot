package com.github.singleton11.domain.error

sealed trait StreamingServiceError extends Product with Serializable {

}

case class NoItemsInResponseError() extends StreamingServiceError

case class NoArtistFoundError() extends StreamingServiceError

case class NoAuthTokenDefined() extends StreamingServiceError
