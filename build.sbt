ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.last
  case path if path.endsWith("/module-info.class") => MergeStrategy.last
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

val http4sVersion = "0.23.18"
lazy val root = (project in file("."))
  .settings(
    name := "drugscraper-get-all",
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "3.0.0",
    libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "2.17.2",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-circe" % http4sVersion,
    ),
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0",
    libraryDependencies += "co.fs2" %% "fs2-core" % "3.5.0",
    libraryDependencies +=
      "co.fs2" %% "fs2-io" % "3.5.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
    )
  )
