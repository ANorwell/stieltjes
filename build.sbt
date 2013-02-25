name := "stieltjes"

organization := "com.anorwell"

version := "0.1.0"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "io.netty" % "netty" % "3.6.3.Final",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "com.aphyr" % "riemann-java-client" % "0.0.6",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

resolvers ++= Seq(
  "Clojars" at "http://clojars.org/repo",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)
