import sbt.Keys._
import sbt._

object Build extends sbt.Build {

  val sharedSettings = Seq(
    organization := "net.globalwebindex",
    scalaVersion := "2.11.6",
    version := "0.5-SNAPSHOT",
    scalacOptions ++= Seq(
      "-unchecked", "-deprecation", "-feature", "-Xfatal-warnings",
      "-Xlint", "-Xfuture",
      "-Yinline-warnings", "-Ywarn-adapted-args", "-Ywarn-inaccessible",
      "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Yno-adapted-args"
    ),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.typesafeRepo("releases"),
      Resolver.mavenLocal,
    "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
    "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
    "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
    "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
    "Twitter Repository"               at "http://maven.twttr.com",
    "Websudos releases"                at "http://maven.websudos.co.uk/ext-release-local",
    "MVNRepository"                     at "http://mvnrepository.com/artifact"
    ),
    libraryDependencies ++= {
      val akkaVersion = "1.0-RC2"
      val phantomVersion = "1.8.0"
      Seq(
        /* AKKA */
        "com.typesafe.akka" %% "akka-stream-experimental" % akkaVersion,
        "com.typesafe.akka" %% "akka-stream-testkit-experimental" % akkaVersion,
        /* LOGGING & NOTIFICATIONS*/
        "com.typesafe.akka" %% "akka-slf4j" % "2.3.9",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
        "ch.qos.logback" % "logback-classic" % "1.1.2",
        /* OTHERS */
        "org.scalatest" %% "scalatest" % "2.2.4" % "test",
        "com.softwaremill" %% "reactive-kafka" % "0.5.0",
        "com.websudos"  %% "phantom-dsl"                   % phantomVersion,
        "com.websudos"  %% "phantom-connectors"                   % phantomVersion,
        "com.sclasen" %% "akka-kafka" % "0.1.0" % "compile"
      )
    },
    autoCompilerPlugins := true,
    /* Testing */
    testOptions in Test += Tests.Argument("-oDF"),
    parallelExecution in Test := false
  )

  lazy val es = (project in file("src-es"))
    .settings(name := "es")
    .settings(sharedSettings:_*)
    .settings(
      libraryDependencies ++= Seq(
        "com.sksamuel.elastic4s" %% "elastic4s" % "1.5.9"
      )
    )

  lazy val root = (project in file("."))
    .settings(name := "core")
    .settings(sharedSettings:_*)
    .settings(
      fork in run := true,
      fork in runMain := true
    ).dependsOn(es)
}