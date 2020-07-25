package com.github.singleton11.util

import cats.Id
import cats.effect.{ExitCase, Sync}

object testimplicits {
  implicit val syncId: Sync[Id] = new Sync[Id] {
    override def suspend[A](thunk: => Id[A]): Id[A] = thunk

    override def bracketCase[A, B](acquire: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] = use(acquire)

    override def raiseError[A](e: Throwable): Id[A] = throw e

    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] = fa

    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)

    //noinspection ScalaDeprecation
    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = f(a).right.get

    override def pure[A](x: A): Id[A] = x
  }
}
