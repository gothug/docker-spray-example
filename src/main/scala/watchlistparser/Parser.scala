package watchlistparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.openqa.selenium.firefox.FirefoxDriver

case class MovieTitles(list: Array[String])
case class WatchListQuery(link: String)

/**
 * Created by kojuhovskiy on 02/02/15.
 */
class Parser {
  def parse(url: String, firefoxDriver: Option[FirefoxDriver] = None) = {
    val html = Jsoup.connect(url).timeout(5000).get()

    val imgMovies = html.body().getElementsByClass("lister-list").first().getElementsByTag("img")

    val movieTitles = imgMovies.toArray(new Array[Element](imgMovies.size)).map(_.attr("alt"))

    MovieTitles(movieTitles)
  }
}

object Parser {
  def main(args: Array[String]) = {
    val parser = new Parser()

//    val url = "http://www.imdb.com/user/ur9112878/watchlist?ref_=wt_nv_wl_all_0"
    val url = "http://www.quickproxy.co.uk/index.php?q=aHR0cDovL3d3dy5pbWRiLmNvbS91c2VyL3VyOTExMjg3OC93YXRjaGxpc3Q%2FcmVmXz13dF9udl93bF9hbGxfMA%3D%3D&hl=2ed"

    val titles = parser.parse(url)
  }
}
