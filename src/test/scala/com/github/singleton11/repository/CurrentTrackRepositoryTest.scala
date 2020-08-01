package com.github.singleton11.repository

import cats.Id
import com.github.singleton11.algebra.CurrentTrackAlgebra
import com.github.singleton11.util.testimplicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.specs2.mutable.Specification

class CurrentTrackRepositoryTest extends Specification {

  "when CurrentTrackRepository.get return empty string" >> {

    object TestCurrentTrackAlgebra {
      val IdInterpreter: CurrentTrackAlgebra[Id, String] = new CurrentTrackAlgebra[Id, String] {
        override def getCurrentTrack: Id[String] = ""
      }
    }


    val repository = new CurrentTrackRepository[Id](TestCurrentTrackAlgebra.IdInterpreter, Slf4jLogger.getLogger[Id])

    "should return nothing" >> {
      repository.get must be empty
    }
  }

  "when CurrentTrackRepository.get return correct html with track" >> {
    object TestCurrentTrackAlgebra {
      val IdInterpreter: CurrentTrackAlgebra[Id, String] = new CurrentTrackAlgebra[Id, String] {
        override def getCurrentTrack: Id[String] = "<title>Red Skies - Concept Citizen</title>"
      }
    }

    val repository = new CurrentTrackRepository[Id](TestCurrentTrackAlgebra.IdInterpreter, Slf4jLogger.getLogger[Id])

    "should return correct CurrentTrack" >> {
      repository.get.get.track must beEqualTo("Concept Citizen")
      repository.get.get.artist must beEqualTo("Red Skies")
    }
  }
}
