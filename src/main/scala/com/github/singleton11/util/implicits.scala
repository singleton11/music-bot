package com.github.singleton11.util

object implicits {

  implicit class StringUtils(s: String) {
    def indexAfter(what: String): Option[Int] = {
      val index = s.indexOf(what)
      index match {
        case -1 => Option.empty[Int]
        case i => Some(i + what.length)
      }
    }

    def indexBefore(what: String): Option[Int] = {
      val index = s.indexOf(what)
      index match {
        case -1 => Option.empty[Int]
        case i => Some(i)
      }
    }
  }

}
