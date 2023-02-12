import GetAllDrugTestUrls.getApiCallUrls
import GetDrugTestsJson.{Output, getDrugTestsJson}
import UploadJsonToBucket.uploadJsonToBucket
import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxParallelTraverse1
import com.google.cloud.storage.{Blob, Option => _}
import io.circe.Json
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

import scala.concurrent.duration.{Duration, SECONDS}

/** Scrapes every drug test on drugsdata.org by calling the drugscrape api in parallel to quickly traverse the entire
  * website
  */
object Main extends IOApp.Simple {
  val run: IO[Unit] = {
    val date: String = java.time.LocalDate.now().toString
    val outputs: List[Output] = getApiCallUrls.zipWithIndex.map {
      case (url, index) =>
        Output("scrape_" + date + s"_$index.json", url)
    }

    EmberClientBuilder
      .default[IO]
      .withTimeout(Duration(1000, SECONDS))
      .withIdleConnectionTime(Duration(1000, SECONDS))
      .withIdleTimeInPool(Duration(1000, SECONDS))
      .build
      .use { client: Client[IO] =>
        outputs
          .parTraverse((output: Output) => {
            getDrugTestsJson(client, output)
              .map((json: Json) => uploadJsonToBucket(output.fileName, json))
          })
          .map((_: List[Option[Blob]]) => ())
      }
  }

}
