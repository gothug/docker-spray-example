package moviesearch

import java.net.URLEncoder

import org.jsoup.Jsoup
import org.openqa.selenium.firefox.FirefoxDriver

import scala.util.{Failure, Success, Try}

/**
 * Created by kojuhovskiy on 21/01/15.
 */
case class KickassQuery(title: String, titleRus: Option[String], year: Int) extends MovieQuery {
  def doQuery(firefoxDriver: Option[FirefoxDriver] = None): MovieQueryResult = {
    logInfo(s"Handling kickass movie query - ${this.toString}")

    val baseUrl = "https://kickass.to"

    val searchRoot = s"$baseUrl/usearch"

    val url = searchRoot + "/" + URLEncoder.encode(title, "UTF-8") + " " + year
//    val url = searchRoot + "/" + title + " " + year

    val result: Try[MovieQueryResult] =
      Try {
        val html = Jsoup.connect(url).timeout(0).get()

        val torrents = html.body().getElementsByClass("torrentname")

        val relHref = torrents.get(0).getElementsByTag("a").get(0).attr("href")
        val listHtml = html.html()

        val searchResultUrl = searchRoot + "/" + title + " " + year + "/?field=seeders&sorder=desc"

        MovieQueryResult(searchResultUrl, "obsolete field")
      }

    result match {
      case Success(movieQueryResult) => movieQueryResult
      case Failure(exception)        =>
        if (("Status=404".r findFirstIn exception.toString).isDefined) {
          MovieQueryResult("NOT FOUND","")
        }
        else {
          throw exception
        }
    }
  }
}

object KickassQuery {
  def main(args: Array[String]) = {
//    val kq = KickassQuery("Cuban Fury", None, 2014)
    val kq = KickassQuery("Kidnapping Mr. Heineken", None, 2015)
//    val kq = KickassQuery("The Slaughter Rule", None, 2002)
    val result = kq.doQuery()

    println(result)
  }
}
