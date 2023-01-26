ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "drugscraper",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.3",
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "3.0.0"
    )
