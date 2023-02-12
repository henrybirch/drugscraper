FROM sbtscala/scala-sbt:openjdk-oraclelinux8-11.0.16_1.8.1_3.2.1
COPY target/scala-2.13/scrape-all.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","./app.jar"]