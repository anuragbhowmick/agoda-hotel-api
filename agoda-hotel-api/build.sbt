name := """agoda-hotel-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.novocode" % "junit-interface" % "0.8" % "test->default",
  "org.mockito" % "mockito-all" % "1.8.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)


fork in run := true