ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val scrapeAll = (project in file("scrape-all")).settings(
  libraryDependencies ++= Seq(
    dependencies.catsCore,
    dependencies.googleCloudStorage,
    dependencies.http4sCirce,
    dependencies.http4sDsl,
    dependencies.http4sEmberClient,
    dependencies.http4sEmberServer,
    dependencies.circeCore,
    dependencies.circeGeneric,
    dependencies.circeParser,
    dependencies.scalaScraper,
    dependencies.fs2Core,
    dependencies.fs2Io
  ),
  assembly / assemblyMergeStrategy := {
    case PathList("module-info.class")               => MergeStrategy.last
    case path if path.endsWith("/module-info.class") => MergeStrategy.last
    case x =>
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
  }
)
lazy val scrapeApi = (project in file("scrape-api")).settings(
  libraryDependencies ++= Seq(
    dependencies.scalaScraper,
    dependencies.http4sDsl,
    dependencies.http4sCirce,
    dependencies.http4sEmberClient,
    dependencies.http4sEmberServer,
    dependencies.circeCore,
    dependencies.circeParser,
    dependencies.circeGeneric
  ),
  assembly / assemblyMergeStrategy := {
    case PathList("module-info.class")               => MergeStrategy.last
    case path if path.endsWith("/module-info.class") => MergeStrategy.last
    case x =>
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
  }
)

lazy val circeVersion = "0.14.4"
lazy val http4sVersion = "1.0.0-M37"

lazy val dependencies = new {
  val scalaScraper = "net.ruippeixotog" %% "scala-scraper" % "3.0.0"
  val googleCloudStorage =
    "com.google.cloud" % "google-cloud-storage" % "2.18.0"
  val http4sCirce = "org.http4s" %% "http4s-circe" % http4sVersion
  val catsCore = "org.typelevel" %% "cats-core" % "2.9.0"
  val fs2Core = "co.fs2" %% "fs2-core" % "3.6.1"
  val fs2Io = "co.fs2" %% "fs2-io" % "3.6.1"
  val http4sEmberClient = "org.http4s" %% "http4s-ember-client" % http4sVersion
  val http4sEmberServer = "org.http4s" %% "http4s-ember-server" % http4sVersion
  val http4sDsl = "org.http4s" %% "http4s-dsl" % http4sVersion
  val circeCore = "io.circe" %% "circe-core" % circeVersion
  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val circeParser = "io.circe" %% "circe-parser" % circeVersion
}
