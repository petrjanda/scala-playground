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
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "com.softwaremill" %% "reactive-kafka" % "0.5.0"
)
    