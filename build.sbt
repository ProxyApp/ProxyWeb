name := """proxyweb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.scalacheck"                %% "scalacheck"                       % "1.11.6",
  "com.sksamuel.scrimage"         %% "scrimage-core"                    % "1.4.2",
  "com.sksamuel.scrimage"         %% "scrimage-canvas"                  % "1.4.2",
  "com.sksamuel.scrimage"         %% "scrimage-filters"                 % "1.4.2"
)
