package ty.pop.sippi.util
import ty.pop.sippi.model.Cart

import scalikejdbc._

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true;";

  // Initialises JDBC driver & connection pool
  Class.forName(derbyDriverClassname)

  // Username and password
  ConnectionPool.singleton(dbURL, "sippi_admin", "scalapower")

  // Ad-hoc session provider on the REPL
  implicit val session = AutoSession
}

object Database extends Database{
  // Checks if there is an existing database is present
  def presentCartDB(): Boolean = {
    DB getTable "cart" match {
      case Some(x) => true
      case None => false
    }
  }

  def presentCartItemsDB(): Boolean = {
    DB getTable "cartItems" match {
      case Some(x) => true
      case None => false
    }
  }

  def setupDB() = {
    // To set up database
    if (!presentCartDB) {
      Cart.initialiseCartTable()
    }
    if (!presentCartItemsDB()) {
      Cart.initialiseCartItemsTable()
    }
  }
}
