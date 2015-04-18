name := """proxyweb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-J-Xss6M")

resolvers ++= Seq(
  "anormcypher" at "http://repo.anormcypher.org/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "io.argonaut"                   %% "argonaut"                         % "6.0.4",
  "org.scalacheck"                %% "scalacheck"                       % "1.11.6",
  "org.anormcypher"               %% "anormcypher"                      % "0.6.0"
)
