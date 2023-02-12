import cats.effect.IO
import io.circe.Json
import org.http4s.circe.jsonDecoder
import org.http4s.client.Client
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Headers, Request, Uri}

object GetDrugTestsJson {
  val token = GetGoogleBearerToken.getGoogleBearerToken()

  /** Calls the drugscrape api belonging to the output case object
    *
    * @param client web client
    * @param output output case object
    * @return json
    */
  def getDrugTestsJson(client: Client[IO], output: Output): IO[Json] = {
    val headers: Headers = Headers(
      Authorization(Credentials.Token(AuthScheme.Bearer, token))
    )
    val request: Request[IO] = Request[IO](
      uri = Uri.fromString(output.apiCallUrl).toOption.get,
      headers = headers
    )

    client.expect[Json](request)
  }

  /** Return a list of urls that collectively point to pages that contain all drug tests on drugsdata.org.
    *
    * @param numInstances the number of drugsdata api instances desired to compute the web scrape
    * @return List[urlStrings]
    */

  case class Output(fileName: String, apiCallUrl: String)
}
