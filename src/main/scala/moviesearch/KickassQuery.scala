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

    val searchUrl = searchRoot + "/" + title + " " + year + " " +
      "category:movies -trailer -official seeds:0"

    val result: Try[MovieQueryResult] =
      Try {
        val html = Jsoup.connect(searchUrl).timeout(0).get()

        val torrents = html.body().getElementsByClass("torrentname")

        val relHref = torrents.get(0).getElementsByTag("a").get(0).attr("href")
        val listHtml = html.html()

        val searchResultUrl = searchUrl + "?field=seeders&sorder=desc"

        MovieQueryResult(Some(searchResultUrl))
      }

    result match {
      case Success(movieQueryResult) => movieQueryResult
      case Failure(exception)        =>
        if (("Status=404".r findFirstIn exception.toString).isDefined) {
          MovieQueryResult(None)
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
