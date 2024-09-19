package ty.pop.sippi.model

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.image.Image

class Drink(drinkNameS: String, priceD: Double, descS: String, sugarI: Int, commentS: String, imagePath: String) {
  def this() = this("", 0, "", 0, "", "")
  var drinkName   = new StringProperty(drinkNameS)
  var price       = ObjectProperty(priceD)
  var description = new StringProperty(descS)
  var sugar       = ObjectProperty(sugarI)
  var comment     = new StringProperty(commentS)
  var image       = ObjectProperty(new Image(imagePath))
}

object Drink {
  def apply(drinkName: String, sugarLevel: Int, comment: String, price: Double, quantity: Int): Drink = {
    new Drink(drinkName, price, "", sugarLevel, comment, "/images/sippi_logo.png")
  }
}