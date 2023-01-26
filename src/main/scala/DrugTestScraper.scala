import net.ruippeixotog.scalascraper
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{element,
  text, texts}
import org.jsoup.Connection

import java.sql.Date
import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

class DrugTestScraper(drugsPage: String) {
  private val browser = new JsoupBrowser {
    override def requestSettings(conn: Connection): Connection =
      conn.timeout(30 * 60000)
  }
  private val tests = browser.get(drugsPage)
  private val drugsDataRootUrl = "https://www.drugsdata.org/"

  private def getWebsiteRecord(rowElement: scalascraper.model.Element) = {
    val sampleNameElement = (rowElement >> element(".sample-name"))

    val singleDrugTestPage = browser.get(getDrugTestUrl(sampleNameElement))

    val detailsModule = singleDrugTestPage >> element(".DetailsModule")

    val rightTbody =
      detailsModule >> element(
        ".TabletDataRight"
        ) >> element("tbody")

    val leftTbody = detailsModule >> element(
      ".TabletDataLeft"
      ) >> element("tbody")

    val id = leftTbody.select(":eq(1)").head.text.toIntOption

    val soldAs =
      (sampleNameElement >?> text(".sold-as"))

    val sampleName =
      (sampleNameElement >?> text("a"))

    val substances =
      (rowElement >?> texts(".Substance li"))

    val amounts = {
      val amounts = rowElement >?> texts(".Amounts li")
      for (strAmounts <- amounts) yield strAmounts.map(_.toInt)
    }

    val testDate = {
      val scrapedStr = rightTbody.select(":eq(1)").head.text
      val formatter = DateTimeFormatter.ofPattern("MMM dd, uuuu")
      try {
        val localDate = LocalDate.parse(scrapedStr, formatter)
        Some(Date.valueOf(localDate))
      } catch {
        case e: DateTimeParseException => None
      }
    }

    val srcLocation = getDrugTestTbodyAttribute(rightTbody, 2)

    val submitterLocation = getDrugTestTbodyAttribute(rightTbody, 3)

    val colour = getDrugTestTbodyAttribute(rightTbody, 4)

    val size = getDrugTestTbodyAttribute(rightTbody, 5)

  }

  private def getDrugTestUrl(
                              sampleNameElementInRow: scalascraper.model.Element
                            ): String =
    drugsDataRootUrl + (sampleNameElementInRow >> element("a"))
      .attr("href")

  private def getDrugTestTbodyAttribute(
                                         tbody: scalascraper.model.Element,
                                         index: Int
                                       ): String =
    tbody.select(s":eq($index)").head.children.tail.head.text

  case class WebsiteRecord(Option: Option[Int], soldAs: Option[String],
                           pictureUrl: Option[String],
                           sampleName: Option[String],
                           substances: Option[List[String]],
                           amounts: Option[List[Double]],
                           testDate: Option[Date],
                           srcLocation: Option[String],
                           submitterLocation: Option[String],
                           colour: Option[String], size: Option[String])


}
