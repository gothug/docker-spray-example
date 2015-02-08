package moviesearch

import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Created by kojuhovskiy on 22/01/15.
 */
case class RutrackerQuery(title: String, titleRus: Option[String], year: Int) extends MovieQuery {
  def doQuery(firefoxDriver: Option[FirefoxDriver] = None): MovieQueryResult = {
    logInfo("doQuery(): STARTED")

    val driver = firefoxDriver.getOrElse(new FirefoxDriver)

    val url = "http://rutracker.org/forum/index.php"

    driver.get(url)

    val queryString = titleRus.getOrElse(title) + " " + year

    driver.findElementById("search-text").sendKeys(queryString)

    driver.findElementByXPath("//form[@id='quick-search']/input[@type='submit']").click()

    val downloadsSortElement = driver.findElementByXPath("//th[@title='Торрент скачан']")
    downloadsSortElement.click()
    downloadsSortElement.click()

    val rootXPath = "//table[@class='forumline tablesorter']"
    val filmListHTML = driver.findElementByXPath(rootXPath).getAttribute("innerHTML")

    val link =
      driver.findElementByXPath(
        rootXPath + "/tbody/tr/td[contains(@class, 't-title')]/div/a"
      ).getAttribute("href")

    logInfo("doQuery(): ENDED")
    MovieQueryResult(link, filmListHTML)
  }
}
