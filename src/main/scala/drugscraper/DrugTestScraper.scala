package drugscraper

import drugscraper.DrugTestScraper._
import net.ruippeixotog.scalascraper
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{element,
  text, texts}
import org.jsoup.Connection

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

class DrugTestScraper(drugsPage: String) {
  private val browser = new JsoupBrowser {
    override def requestSettings(conn: Connection): Connection =
      conn.timeout(30 * 60000)
  }
  private val tests = browser.get(drugsPage)

  def getAllDrugTests(): Seq[WebsiteRecord] = {
    getAllRowElements().map(el => getWebsiteRecord(el)).toSeq
  }

  def getAllRowElements(): Iterable[scalascraper.model.Element] =
    (tests >> element("#MainResults") >> element("tbody")).children

  def getWebsiteRecord(rowElement: scalascraper.model.Element) = {
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

    val pictureUrl = (detailsModule >?> element(".TabletPhoto-set") >?>
      element(".TabletMedium")).flatten.map(el => el.attr("src"))

    val soldAs =
      (sampleNameElement >?> text(".sold-as"))

    val sampleName =
      (sampleNameElement >?> text("a"))

    val substances =
      (rowElement >?> texts(".Substance li")).map(_.toList)

    val amounts = {
      val amounts = rowElement >?> texts(".Amounts li")
      for (strAmounts <- amounts) yield strAmounts.map(_.toDoubleOption).toList
    }

    val testDate = parseDrugTestDateStrToDate(rightTbody.select(":eq(1)")
                                                        .head.text)

    val srcLocation = getDrugTestTbodyAttribute(rightTbody, 2)

    val submitterLocation = getDrugTestTbodyAttribute(rightTbody, 3)

    val colour = getDrugTestTbodyAttribute(rightTbody, 4)

    val size = getDrugTestTbodyAttribute(rightTbody, 5)

    WebsiteRecord(id, soldAs, pictureUrl, sampleName, substances, amounts,
                  testDate, srcLocation, submitterLocation, colour, size)

  }


}

object DrugTestScraper {
  private val drugsDataRootUrl = "https://www.drugsdata.org/"

  private def getDrugTestUrl(
                              sampleNameElementInRow: scalascraper.model
                              .Element
                            ): String =
    drugsDataRootUrl + (sampleNameElementInRow >> element("a"))
      .attr("href")

  private def getDrugTestTbodyAttribute(
                                         tbody: scalascraper.model.Element,
                                         index: Int
                                       ): Option[String] = {
    try {
      Some(tbody.select(s":eq($index)").head.children.tail.head.text)
    } catch {
      case e: NoSuchElementException => None
    }
  }

  private def parseDrugTestDateStrToDate(scrapedStr: String): Option[String] = {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, uuuu")
    try {
      val localDate = LocalDate.parse(scrapedStr, formatter).toString
      Some(localDate)
    } catch {
      case e: DateTimeParseException => None
    }
  }

  case class WebsiteRecord(id: Option[Int], soldAs: Option[String],
                           pictureUrl: Option[String],
                           sampleName: Option[String],
                           substances: Option[List[String]],
                           amounts: Option[List[Option[Double]]],
                           testDate: Option[String],
                           srcLocation: Option[String],
                           submitterLocation: Option[String],
                           colour: Option[String], size: Option[String])

}
