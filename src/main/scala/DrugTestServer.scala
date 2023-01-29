import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.IpLiteralSyntax
import drugscraper.DrugTestScraper
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router

import java.net.URLDecoder

object DrugTestServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val app = Router("/" -> drugRoutes).orNotFound
    EmberServerBuilder.default[IO].withHost(ipv4"0.0.0.0").withPort(port"8080").withHttpApp(app).build.use(_ => IO.never).as(ExitCode.Success)
  }

  def drugRoutes: HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "scrape" / encodedUrl =>
        val drugResults = {
          val url = URLDecoder.decode(encodedUrl, "utf-8")
          new DrugTestScraper(url).getAllDrugTests().asJson
        }
        Ok(drugResults)
      case _ => NotFound("Invalid query")
    }
  }
}


