package ty.pop.sippi
import ty.pop.sippi.view.EditDrinkDialogController
import ty.pop.sippi.model._
import ty.pop.sippi.util.Database

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp {
  Database.setupDB()
  // Gets the main (root) layout of the app to be loaded
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  // Stage for the frame of the whole app
  stage = new PrimaryStage {
    title = "Sippi Ordering App"
    icons += new Image(getClass.getResourceAsStream("/images/sippi_logo.png"))
    scene = new Scene {
      root = roots
    }
  }

  // Sets the minimum window size
  stage.setMinWidth(600)
  stage.setMinHeight(450)

  // Launches the Welcome page
  def showWelcome(): Unit = {
    val resource = getClass.getResource("view/Welcome.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  // Launches the Cart page
  def showCart(): Unit = {
    val resource = getClass.getResource("view/Cart.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  // Launches the Menu page
  def showMenu(): Unit = {
    val resource = getClass.getResource("view/Menu.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  // Launches the CustomiseItem pop-up
  def showEditDrinkDialog(drink: Drink): Unit = {
    val resource = getClass.getResourceAsStream("view/EditDrinkDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2  = loader.getRoot[jfxs.Parent]
    val control = loader.getController[EditDrinkDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene {
        root = roots2
      }
    }

    // Pass the drink to the controller
    control.dialogStage = dialog
    control.drink = drink
    dialog.showAndWait()
    control.confirmClicked
  }



  val drinkData = new ObservableBuffer[Drink]()
  drinkData += new Drink("Milk Tea", 8.90, "Classic milk tea with brown sugar", 4, "", "/images/milk_tea.jpg")
  drinkData += new Drink("Matcha Latte", 9.90, "Grassy matcha with milk", 4, "", "/images/matcha_latte.jpg")
  drinkData += new Drink("Lemon Tea", 6.90, "Refreshing black tea with lemon squeeze", 4, "", "/images/lemon_tea.jpg")

  val cart = new Cart()
  var cartItems = Cart.getCartItems()

  // Calls the Welcome page when the app is launched
  showWelcome()
}