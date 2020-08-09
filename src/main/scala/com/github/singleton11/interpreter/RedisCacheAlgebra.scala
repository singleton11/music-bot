package com.github.singleton11.interpreter

import cats.effect.{IO, _}
import com.github.singleton11.algebra.CacheAlgebra
import com.github.singleton11.domain.NoAuthTokenInCache
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout._

import scala.concurrent.ExecutionContext.global

object RedisCacheAlgebra {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(global)

  val CURRENT_AUTH_TOKEN_KEY = "currentAuthToken"

  /**
   * Get IO interpreter for cache algebra.
   *
   * @param redisHost in format redis://host
   * @return CacheAlgebra interpreter instance
   */
  def IOInterpreter(redisHost: String): CacheAlgebra[IO] = new CacheAlgebra[IO] {
    override def putAuthToken(authToken: String): IO[Unit] = Redis[IO].utf8(redisHost)
      .use { cmd => cmd.set(CURRENT_AUTH_TOKEN_KEY, authToken) }

    override def getAuthToken: IO[String] = Redis[IO].utf8(redisHost)
      .use { cmd => {
        cmd.get(CURRENT_AUTH_TOKEN_KEY).flatMap {
          case Some(value) => IO.pure(value)
          case None => IO.raiseError(NoAuthTokenInCache())
        }
      }
      }
  }

}
