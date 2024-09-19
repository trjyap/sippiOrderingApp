package ty.pop.sippi.view
import ty.pop.sippi.MainApp

import scalafxml.core.macros.sfxml

@sfxml
class WelcomeController {
  def getMenu(): Unit = {
    MainApp.showMenu()
  }

  def getCart(): Unit = {
    MainApp.showCart()
  }
}