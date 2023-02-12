ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"
val circeVersion = "0.14.1"
val http4sVersion = "1.0.0-M37"

assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.last
  case path if path.endsWith("/module-info.class") => MergeStrategy.last
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
  .settings(
    name := "drugscraper",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "3.0.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion
    ),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "1.0.0-M37",
      "org.http4s" %% "http4s-ember-client" % "1.0.0-M37",
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
  )
