name := "music-bot"

version := "0.1"

scalaVersion := "2.13.3"
val http4sVersion = "0.21.5"

scalacOptions ++= Seq(
  "-feature",
  "-language:_",
  "-Ymacro-annotations"
)

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.4"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "4.10.0" % "test")

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.3.0-alpha5"

libraryDependencies ++= Seq(
  "io.chrisdavenport" %% "log4cats-core" % "1.1.1",
  "io.chrisdavenport" %% "log4cats-slf4j" % "1.1.1",
)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.13.0",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.13.0"
)

libraryDependencies +=
  "org.typelevel" %% "cats-tagless-macros" % "0.11"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)