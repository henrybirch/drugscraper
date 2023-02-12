import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.element

import java.net.URLEncoder
import scala.annotation.tailrec

object GetAllDrugTestUrls {
  private val browser: JsoupBrowser = new JsoupBrowser()
  private val scrapeApiUrl: String = sys.env("scrapeApiUrl")

  /** Return all api urls that each will return json scraped from drugsdata
    *
    * @return List[urlStrings]
    */
  def getApiCallUrls: List[String] = {
    val encodedUrls: List[String] =
      getAllDrugTestUrls(500).map(URLEncoder.encode(_, "utf-8"))
    encodedUrls.map((url: String) => {
      s"$scrapeApiUrl/scrape/$url"
    })
  }

  /** Return a list of urls that collectively point to pages that contain all drug tests on drugsdata.org.
    *
    * @param numInstances the number of drugsdata api instances desired to compute the web scrape
    * @return List[urlStrings]
    */
  private def getAllDrugTestUrls(numInstances: Int): List[String] = {
    val homePage: JsoupBrowser.JsoupDocument =
      browser.get("https://www.drugsdata.org")
    val numEntries: Int =
      (homePage >> element(".Results")).text.split(" ")(0).toInt
    val numEntriesPerCall: Int = numEntries / numInstances
    val numEntriesRemaining: Int = numEntries % numInstances

    def getPageNumbers: List[Int] = {
      @tailrec
      def go(acc: List[Int], n: Int): List[Int] = {
        if (n == numEntriesRemaining) {
          0 :: acc
        } else {
          val thisIndex: Int = n - numEntriesPerCall
          go(thisIndex :: acc, thisIndex)
        }
      }

      go(Nil, numEntries)
    }

    getPageNumbers.map(getScrapeUrl(_, numEntriesPerCall))
  }

  /** Return a url that points to a page that contains drug tests starting from start and that contains max tests
    *
    * @param start
    * @param max
    * @return
    */
  private def getScrapeUrl(start: Int, max: Int): String =
    s"https://www.drugsdata.org/index.php?sort=DatePublishedU+desc&start=$start&a=&search_field=-&m1=-1&m2=-1&sold_as_ecstasy=both&datefield=tested&max=$max"

}
