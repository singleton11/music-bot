name := "music-bot"

version := "0.1"

scalaVersion := "2.13.3"
val http4sVersion = "0.21.5"

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.4"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)