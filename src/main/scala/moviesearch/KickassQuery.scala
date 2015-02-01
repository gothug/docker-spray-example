package moviesearch

import java.net.URLEncoder

import org.jsoup.Jsoup
import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Created by kojuhovskiy on 21/01/15.
 */
case class KickassQuery(title: String, titleRus: Option[String], year: Int) extends MovieQuery {
  def doQuery(firefoxDriver: Option[FirefoxDriver] = None): Result = {
    val root = "https://kickass.so/usearch"

    val url = root + "/" + URLEncoder.encode(title, "UTF-8") + " " + year

    val html = Jsoup.connect(url).get()

    val torrents = html.body().getElementsByClass("torrentname")

    val href = torrents.get(0).getElementsByTag("a").get(0).attr("href")
    val listHtml = html.html()

    Result(href, listHtml)
  }
}

object KickassQuery {
  def main(args: Array[String]) = {
    val result = KickassQuery("leviathan", None, 2014).doQuery()

    println(result)
  }
}
