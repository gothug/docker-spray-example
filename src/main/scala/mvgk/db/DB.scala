package mvgk.db

import java.util.Properties

import mvgk.config.Config
import mvgk.db.MyPostgresDriver.simple._
import Database.dynamicSession
import mvgk.db.model.Tables._

import scala.slick.jdbc.StaticQuery
import scala.util.Try

/**
 * @author Got Hug
 */
object DB {
  val driver = "org.postgresql.Driver"
  val host = Config.db.host
  val name = Config.db.name
  val user = Config.db.user
  val password = Config.db.password
  val url = "jdbc:postgresql"

  val tables = List(film, resource, search)
  val db = Database.forURL(s"$url://$host/$name", user, password, new Properties(), driver)
  val purePostgres = Database.forURL(s"$url:?user=$user&password=$password", driver = driver)

  def create(): Unit = {
    purePostgres.withDynSession {
      StaticQuery.updateNA(s"create database $name").execute
    }
//    createEnums()
  }

  def safeDrop(): Unit = {
    purePostgres.withDynSession {
      StaticQuery.updateNA(s"drop database if exists $name").execute
    }
  }

  //todo: make it safe?
  def createTables(): Unit = {
    db.withDynSession {
      tables.reverse.map { table => Try(table.ddl.create)}
    }
  }

//  def createEnums(): Unit = {
//    mov.db.withDynSession {
//      buildCreateSql("Status", Statuses).execute
//      buildCreateSql("Regime", Regimes).execute
//      buildCreateSql("Product", Products).execute
//      buildCreateSql("Platform", Platforms).execute
//    }
//  }

  def dropTables(): Unit = {
    db.withDynSession {
      tables.map { table => Try(table.ddl.drop)}
//      dropEnums()
    }
  }

//  def dropEnums(): Unit = {
//    buildDropSql("platform").execute
//    buildDropSql("product").execute
//    buildDropSql("regime").execute
//    buildDropSql("status").execute
//  }
}
