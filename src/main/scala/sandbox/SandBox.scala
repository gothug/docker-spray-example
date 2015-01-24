package sandbox

import java.net.URLEncoder

import moviesearch.{Query, RutrackerQuery}
import org.jsoup.Jsoup
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit._

import scala.reflect.io.File

/**
 * Created by kojuhovskiy on 19/01/15.
 */
class SandBox {

}

object SandBox {
  def main(args: Array[String]) = {

    val htmlString: String = "<!DOCTYPE html>" ++
      "<html>" ++
      "<head>" ++
      "<title>JSoup Example</title>" ++
      "</head>" ++
      "<body>" ++
      "<table><tr><td><h1>HelloWorld</h1></tr>" ++
      "</table>" ++
      "</body>" ++
      "</html>"

//    val html = Jsoup.parse(htmlString)

//    val html = Jsoup.connect("https://kickass.so/usearch/leviathan%202014/").get()

//    val torrents = html.body().getElementsByClass("torrentname")

//    val href = torrents.get(0).getElementsByTag("a").get(0).attr("href")
//    val listHtml = html.html()

//    val result = RutrackerQuery.doQuery(Query("fountain", Some("фонтан"), 1988))

//    val link = result.link
//    val html = result.html

//    File("out.html").writeAll(html)
//    println("link: " + link)
  }
}
