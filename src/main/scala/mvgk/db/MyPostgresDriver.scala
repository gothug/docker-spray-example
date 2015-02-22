package mvgk.db

import com.github.tminglei.slickpg.{PgArraySupport, PgDateSupport, PgEnumSupport}
import scala.slick.driver.PostgresDriver

object MyPostgresDriver extends PostgresDriver with PgEnumSupport
with PgArraySupport
with PgDateSupport {
  override lazy val Implicit = new Implicits with ArrayImplicits with MyEnumImplicits {}

  override val simple = new SimpleQL with MyEnumImplicits with ArrayImplicits {}

  trait MyEnumImplicits {
//    implicit val statusTypeMapper = createEnumJdbcType("Status", Statuses)
//    implicit val regimeTypeMapper = createEnumJdbcType("Regime", Regimes)
//    implicit val productTypeMapper = createEnumJdbcType("Product", Products)
//    implicit val platformTypeMapper = createEnumJdbcType("Platform", Platforms)
  }
}
