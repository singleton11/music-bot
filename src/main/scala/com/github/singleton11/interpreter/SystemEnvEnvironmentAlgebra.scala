package com.github.singleton11.interpreter

import com.github.singleton11.algebra.EnvironmentAlgebra

object SystemEnvEnvironmentAlgebra {
  val OptionInterpreter: EnvironmentAlgebra[Option, String] = new EnvironmentAlgebra[Option, String] {
    override def getRedisHost: Option[String] = Option(sys.env("REDIS_HOST"))
  }
}
