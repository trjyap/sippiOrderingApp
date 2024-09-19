package ty.pop.sippi.view
import ty.pop.sippi.MainApp

import scalafx.application.Platform
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController {
  // Functions under menu FILE
  def handleHome(): Unit = {
    MainApp.showWelcome()
  }

  def handleMenu(): Unit = {
    MainApp.showMenu()
  }

  def handleCart(): Unit = {
    MainApp.showCart()
  }

  def handleExit(): Unit = {
    Platform.exit()
  }


  // Functions under menu ABOUT
  def handleAbout(): Unit = {
    new Alert(AlertType.Information) {
      initOwner(MainApp.stage)
      title = "About Sippi"
      headerText = "About Sippi"
      contentText = "Sippi is a pop-up drinks store for your convenience\n" +
        "Find us at @sippidrinki on Instagram/Facebook"
    }.showAndWait()
  }

  def handleContact(): Unit = {
    new Alert(AlertType.Information) {
      initOwner(MainApp.stage)
      title = "Contact Us"
      headerText = "Contact Us"
      contentText = "For feedback/suggestions, email to support@sippi.com\n" +
        "Developed by sunwayTim"
    }.showAndWait()
  }
}
