import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxParallelTraverse1
import com.google.auth.oauth2.{
  GoogleCredentials,
  IdTokenCredentials,
  IdTokenProvider
}
import com.google.cloud.storage.{BlobId, BlobInfo, StorageOptions}
import io.circe.Json
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.element
import org.http4s.circe.jsonDecoder
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Headers, Request, Uri}

import java.io.ByteArrayInputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import scala.concurrent.duration.{Duration, SECONDS}

object Main extends IOApp.Simple {
  val outputBucketName = sys.env("outputBucketName")

  val scrapeApiUrl = sys.env("scrapeApiUrl")

  val storage = StorageOptions
    .newBuilder()
    .setProjectId("trans-sunset-370818-4cba431ef717")
    .build()
    .getService()

  val token = {
    val credentials = GoogleCredentials
      .getApplicationDefault()
      .createScoped("https://www.googleapis.com/auth/cloud-platform")
    val idBuilder = IdTokenCredentials
      .newBuilder()
      .setIdTokenProvider(credentials.asInstanceOf[IdTokenProvider])
      .setTargetAudience("https://drugscraper-api-7x2jgthdga-uc.a.run.app")
      .build()
    idBuilder.refresh()
    idBuilder.getIdToken.getTokenValue
  }

  val run = {
    val date = java.time.LocalDate.now().toString
    val outputs = getApiCallUrls.zipWithIndex.map { case (url, index) =>
      Output("scrape_" + date + s"_$index.json", url)
    }

    EmberClientBuilder
      .default[IO]
      .withTimeout(Duration(1000, SECONDS))
      .withIdleConnectionTime(Duration(1000, SECONDS))
      .withIdleTimeInPool(Duration(1000, SECONDS))
      .build
      .use { client =>
        outputs
          .parTraverse(output => {
            saveJson(client, output)
          })
          .map(_ => ())
      }
    /*EmberClientBuilder
      .default[IO]
      .withTimeout(Duration(1000, SECONDS))
      .withIdleConnectionTime(Duration(1000, SECONDS))
      .withIdleTimeInPool(Duration(1000, SECONDS))
      .build
      .use { client =>
        saveJson(
          client,
          Output(
            "waba.json",
            "https://drugscraper-api-7x2jgthdga-uc.a.run.app/scrape/https%3A%2F%2Fwww.drugsdata.org%2Findex.php%3Fsort%3DDatePublishedU%2Bdesc%26start%3D15223%26a%3D%26search_field%3D-%26m1%3D-1%26m2%3D-1%26sold_as_ecstasy%3Dboth%26datefield%3Dtested%26max%3D15"
          )
        )
      }
      .map(_ => ())*/
  }

  def saveJson(client: Client[IO], output: Output) = {
    val headers = Headers(
      Authorization(Credentials.Token(AuthScheme.Bearer, token))
    )
    val request = Request[IO](
      uri = Uri.fromString(output.apiCallUrl).toOption.get,
      headers = headers
    )

    client.expect[Json](request).map(uploadJsonToBucket(output.fileName, _))
  }

  def uploadJsonToBucket(fileName: String, json: Json) = {
    println(json)
    val formattedJsonForBigQuery = {
      val jsonArray = json.asArray
      jsonArray.map(_.map(_.noSpaces))
    }.map(_.mkString("\n"))
    val blobId = BlobId.of(outputBucketName, fileName)
    val blobInfo = BlobInfo.newBuilder(blobId).build()
    val content =
      formattedJsonForBigQuery.map(_.getBytes(StandardCharsets.UTF_8))

    content.map(bytes =>
      storage.createFrom(blobInfo, new ByteArrayInputStream(bytes))
    )
  }

  def getApiCallUrls = {
    val encodedUrls = getAllDrugTestUrls.map(URLEncoder.encode(_, "utf-8"))
    encodedUrls.map(url => {
      s"$scrapeApiUrl/scrape/$url"
    })
  }

  def getAllDrugTestUrls = {
    val browser = new JsoupBrowser()
    val homePage = browser.get("https://www.drugsdata.org")
    val numEntries = (homePage >> element(".Results")).text.split(" ")(0).toInt
    val numInstances = 500
    val numEntriesPerCall = numEntries / numInstances
    val numEntriesRemaining = numEntries % numInstances

    def go(urls: List[String], n: Int): List[String] = {
      if (n == numEntriesRemaining) {
        getScrapeUrl(0, numEntriesRemaining) :: urls
      } else {
        go(
          getScrapeUrl(
            n - numEntriesPerCall,
            numEntriesPerCall
          ) :: urls,
          n - numEntriesPerCall
        )
      }
    }
    go(Nil, numEntries)
  }

  def getScrapeUrl(start: Int, max: Int) =
    s"https://www.drugsdata.org/index.php?sort=DatePublishedU+desc&start=${start}&a=&search_field=-&m1=-1&m2=-1&sold_as_ecstasy=both&datefield=tested&max=${max}"

  case class Output(fileName: String, apiCallUrl: String)

}
