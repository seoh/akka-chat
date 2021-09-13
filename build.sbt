
name := "Akka Chat"
scalaVersion := "2.13.6"

val Versions = new {
  val akka = "2.6.16"
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % Versions.akka,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % Versions.akka % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)
