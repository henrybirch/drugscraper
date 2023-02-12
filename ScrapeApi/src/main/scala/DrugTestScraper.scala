import DrugTestScraper._
import net.ruippeixotog.scalascraper
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{element, text, texts}
import org.jsoup.Connection

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.util.concurrent.TimeUnit

class DrugTestScraper(drugsPage: String) {
  private val tests: JsoupBrowser.JsoupDocument = browser.get(drugsPage)

  /** Returns all drug tests that are present in the url drugsPage
    * @return Seq[WebsiteRecord]
    */
  def getAllDrugTests: Seq[WebsiteRecord] = {
    getAllRowElements
      .map((el: Element) => {
        TimeUnit.SECONDS.sleep(1)
        getWebsiteRecord(el)
      })
      .toSeq
  }

  /** Returns all row elements that contain the detailed drug test URL to be scraped
    * @return Iterable[Element]
    */
  private def getAllRowElements: Iterable[scalascraper.model.Element] =
    (tests >> element("#MainResults") >> element("tbody")).children

  /** Access the row element, then gets the detailed drug page HTML and parses the drug data to a WebsiteRecord
    * @param rowElement
    * @return WebsiteRecord
    */
  private def getWebsiteRecord(
      rowElement: scalascraper.model.Element
  ): WebsiteRecord = {
    val sampleNameElement: Element = rowElement >> element(".sample-name")

    val singleDrugTestPage: JsoupBrowser.JsoupDocument =
      browser.get(getDrugTestUrl(sampleNameElement))

    val detailsModule: Element = singleDrugTestPage >> element(".DetailsModule")

    val rightTbody: Element =
      detailsModule >> element(".TabletDataRight") >> element("tbody")

    val leftTbody: Element =
      detailsModule >> element(".TabletDataLeft") >> element("tbody")

    val id: Option[Int] = leftTbody.select(":eq(1)").head.text.toIntOption

    val pictureUrl: Option[String] =
      (detailsModule >?> element(".TabletPhoto-set") >?>
        element(".TabletMedium")).flatten.map((el: Element) => el.attr("src"))

    val soldAs: Option[String] =
      (sampleNameElement >?> text(".sold-as")).flatMap(
        (_: String).split(": ").lastOption
      )

    val sampleName: Option[String] =
      sampleNameElement >?> text("a")

    val substances: Option[List[String]] =
      (rowElement >?> texts(".Substance li")).map((_: Iterable[String]).toList)

    val amounts: Option[List[Double]] = {
      val amounts: Option[Iterable[String]] =
        rowElement >?> texts(".Amounts li")
      for (strAmounts <- amounts)
        yield strAmounts.map((_: String).toDoubleOption).toList.flatten
    }

    val testDate: Option[String] = parseDrugTestDateStrToDate(
      rightTbody.select(":eq(1)").head.text
    )

    val srcLocation: Option[String] = getDrugTestTbodyAttribute(rightTbody, 2)

    val submitterLocation: Option[String] =
      getDrugTestTbodyAttribute(rightTbody, 3)

    val colour: Option[String] = getDrugTestTbodyAttribute(rightTbody, 4)

    val size: Option[String] = getDrugTestTbodyAttribute(rightTbody, 5)

    WebsiteRecord(
      id,
      soldAs,
      pictureUrl,
      sampleName,
      substances,
      amounts,
      testDate,
      srcLocation,
      submitterLocation,
      colour,
      size
    )

  }

}

object DrugTestScraper {
  private val browser: JsoupBrowser = new JsoupBrowser {
    override def requestSettings(conn: Connection): Connection =
      conn.timeout(30 * 60000)
  }

  private val drugsDataRootUrl: String = "https://www.drugsdata.org/"

  /** Returns the URL for the detailed drug test page from a row element in the main table
    * @param sampleNameElementInRow the component of the row containing the sample name
    * @return drugTestUrl the url to the page containing more drug info
    */
  private def getDrugTestUrl(
      sampleNameElementInRow: scalascraper.model.Element
  ): String =
    drugsDataRootUrl + (sampleNameElementInRow >> element("a"))
      .attr("href")

  /** Returns the item at index from the detailed drug table in the nested page
    * @param tbody detailed drug table
    * @param index index (including 0)
    * @return itemString
    */
  private def getDrugTestTbodyAttribute(
      tbody: scalascraper.model.Element,
      index: Int
  ): Option[String] = {
    try {
      Some(tbody.select(s":eq($index)").head.children.tail.head.text)
    } catch {
      case _: NoSuchElementException => None
    }
  }

  /** Returns an ISO formatted string from a string formatted MMM dd, uuuu
    * @param scrapedStr date scraped from table
    * @return Option[DateString] ISO formatted Option[String]
    */
  private def parseDrugTestDateStrToDate(scrapedStr: String): Option[String] = {
    val formatter: DateTimeFormatter =
      DateTimeFormatter.ofPattern("MMM dd, uuuu")
    try {
      val localDate: String = LocalDate.parse(scrapedStr, formatter).toString
      Some(localDate)
    } catch {
      case _: DateTimeParseException => None
    }
  }

  /** Schema for drug tests to be scraped
    * @param id unique id
    * @param soldAs name submitted by the sender
    * @param pictureUrl href for the photo of the drug
    * @param sampleName name created by the tester
    * @param substances list of chemical substances in order of amount
    * @param amounts list of amounts
    * @param testDate date of test
    * @param srcLocation location string, usually of format City, State or Country
    * @param submitterLocation location string, usually of format City, State or Country
    * @param colour colour
    * @param size size in variable units
    */
  case class WebsiteRecord(
      id: Option[Int],
      soldAs: Option[String],
      pictureUrl: Option[String],
      sampleName: Option[String],
      substances: Option[List[String]],
      amounts: Option[List[Double]],
      testDate: Option[String],
      srcLocation: Option[String],
      submitterLocation: Option[String],
      colour: Option[String],
      size: Option[String]
  )

}
