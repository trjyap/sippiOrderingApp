package ty.pop.sippi.model
import ty.pop.sippi.util.{Database, DateUtil}

import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalikejdbc._

import java.time.LocalDateTime
import scala.util.Try

class Cart() {
  var cartItems = new ObservableBuffer[Drink]()
  var drinkName = new StringProperty("")
  var sugar = IntegerProperty(4)
  var comment = new StringProperty("")
  var price = new ObjectProperty[Double]()
  var totalPrice = new ObjectProperty[Double]()
  var datetime = new ObjectProperty[LocalDateTime]

}


object Cart extends Database {
  var cartItems = new ObservableBuffer[Drink]()
  var totalPrice = 0.0
  var datetime = new ObjectProperty[LocalDateTime]
  var currentCartID: Int = _

  // Create a new cart and set currentCartID
  def createNewCart(): Unit = {
    currentCartID = DB autoCommit { implicit session =>
      sql"""
        insert into cart (CREATED_AT) values (current_timestamp)
      """.updateAndReturnGeneratedKey.apply().toInt
    }
  }

  // Add drink to the cart
  def addDrink(drink: Drink): Unit = {
    if (currentCartID == 0) {
      createNewCart()
    }
    cartItems += drink
  }

  // Remove drink from the cart
  def removeDrink(drink: Drink): Unit = {
    cartItems -= drink
  }

  def updateTime(): Unit = {
    datetime.value = LocalDateTime.now()
  }

  def getDrinks: ObservableBuffer[Drink] = cartItems

  def calculateTotalPrice(): Double = {
    totalPrice = cartItems.map(_.price.value).sum
    val formattedPrice = f"$totalPrice%.2f".toDouble
    formattedPrice
  }

  // Initialising the Cart table in the database
  def initialiseCartTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        create table cart (
          ID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          CREATED_AT timestamp default current_timestamp,
          primary key (ID)
        )
			""".execute.apply()
    }
  }

  // Initialising the CartItems table in the database
  def initialiseCartItemsTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
           create table cartItems (
            CART_ID int not null,
            DRINK_NAME varchar(64),
            SUGAR_LEVEL int,
            COMMENT varchar(200),
            PRICE decimal(10, 2),
            QUANTITY int,
            primary key (CART_ID, DRINK_NAME, SUGAR_LEVEL, COMMENT),
            foreign key (CART_ID) references cart(ID)
           )
      """.execute.apply()
    }
  }

  def getCartItems(): List[Drink] = {
    DB readOnly { implicit session =>
      sql"""
           select * from cartItems
      """.map(rs => Drink(
        rs.string("DRINK_NAME"),
        rs.int("SUGAR_LEVEL"),
        rs.string("COMMENT"),
        rs.double("PRICE"),
        rs.int("QUANTITY")
      )).list.apply()
    }
  }

  // Checks if cart has identical entries
  def entryExists(drink: Drink) : Boolean =  {
    DB readOnly { implicit session =>
      sql"""
				select * from cartItems where
				CART_ID = ${Cart.currentCartID} and DRINK_NAME = ${drink.drinkName.value}
				and SUGAR_LEVEL = ${drink.sugar.value} and COMMENT = ${drink.comment.value}
			""".map(rs => rs.string("DRINK_NAME")).first.apply()
    } match {
      case Some(x) => true
      case None => false
    }
  }

  // Saving entry to the database
  def save(drink: Drink): Unit = {
    if (entryExists(drink)) {
      // Updates the quantity and price for an existing entry
      DB autoCommit { implicit session =>
        sql"""
         update cartItems set QUANTITY = QUANTITY + 1, PRICE = PRICE + ${drink.price.value}
         where CART_ID = ${Cart.currentCartID} and DRINK_NAME = ${drink.drinkName.value}
         and SUGAR_LEVEL = ${drink.sugar.value} and COMMENT = ${drink.comment.value}
      """.update.apply()
      }
    } else {
      DB autoCommit { implicit session =>
        sql"""
           insert into cartItems (CART_ID, DRINK_NAME, SUGAR_LEVEL, COMMENT, PRICE, QUANTITY)
           values (${Cart.currentCartID}, ${drink.drinkName.value}, ${drink.sugar.value}, ${drink.comment.value}, ${drink.price.value}, 1)
        """.update.apply()
      }
    }
  }

  // Removing entry from the database
  def remove(drink: Drink): Unit = {
    if (entryExists(drink)) {
      Try(DB autoCommit { implicit session =>
        sql"""
				  delete from cartItems where
				  DRINK_NAME = ${drink.drinkName.value} and SUGAR_LEVEL = ${drink.sugar.value} and COMMENT = ${drink.comment.value}
				""".update.apply()
      })
    } else {
      throw new Exception("This entry does not exist in the table")
    }
  }
}