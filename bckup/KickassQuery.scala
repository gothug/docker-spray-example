package moviesearch

import java.net.URLEncoder

import com.typesafe.scalalogging.slf4j.Logger
import org.jsoup.Jsoup
import org.openqa.selenium.firefox.FirefoxDriver
import org.slf4j.LoggerFactory

/**
 * Created by kojuhovskiy on 21/01/15.
 */
case class KickassQuery(title: String, titleRus: Option[String], year: Int) extends MovieQuery {
  val logger = Logger(LoggerFactory.getLogger("movieQuery"))

  def doQuery(firefoxDriver: Option[FirefoxDriver] = None): MovieQueryResult = {
    logger.info("Handling kickass movie query")

    val root = "https://kickass.so/usearch"

    val url = root + "/" + URLEncoder.encode(title, "UTF-8") + " " + year

    val html = Jsoup.connect(url).get()

    val torrents = html.body().getElementsByClass("torrentname")

    val href = torrents.get(0).getElementsByTag("a").get(0).attr("href")
    val listHtml = html.html()

    logger.info("Finished handling kickass movie query")

    MovieQueryResult(href, listHtml)
  }
}

object KickassQuery {
  def main(args: Array[String]) = {
    val result = KickassQuery("leviathan", None, 2014).doQuery()

    println(result)
  }
}
