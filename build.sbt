name := "test"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature", "-Xfatal-warnings",
  "-Xlint", "-Xfuture",
  "-Yinline-warnings", "-Ywarn-adapted-args", "-Ywarn-inaccessible",
  "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Yno-adapted-args"
)

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
)

val phantomVersion = "1.8.0"

libraryDependencies ++= Seq(
  "com.softwaremill" %% "reactive-kafka" % "0.5.0",
  "com.websudos"  %% "phantom-dsl"                   % phantomVersion,
  "com.websudos"  %% "phantom-connectors"                   % phantomVersion
)
    