package ty.pop.sippi.view
import ty.pop.sippi.MainApp
import ty.pop.sippi.model._

import scalafx.scene.control.{Alert, Label, TableColumn, TableView}
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.ImageView
import scalafxml.core.macros.sfxml

@sfxml
class MenuController(
                    private val menuTable: TableView[Drink],
                    private val drinkNameColumn: TableColumn[Drink, String],
                    private val priceColumn: TableColumn[Drink, Double],
                    private val drinkNameLabel: Label,
                    private val descLabel: Label,
                    private val priceLabel: Label,
                    private val sugarLabel: Label,
                    private val commentLabel: Label,
                    private val imageView: ImageView
                    ) {

  menuTable.items = MainApp.drinkData
  drinkNameColumn.cellValueFactory = {_.value.drinkName}
  priceColumn.cellValueFactory = {_.value.price}


  private def showDrinkDetails(drink : Option[Drink]): Unit = {
    drink match {
      case Some(drink) =>
        // Fill the labels with info from the person object.
        drinkNameLabel.text <== drink.drinkName
        priceLabel.text     = drink.price.value.toString
        descLabel.text      <== drink.description
        sugarLabel.text     = drink.sugar.value.toString
        commentLabel.text   <== drink.comment
        // Setting the image
        imageView.image = drink.image.get()

      case None =>
        // Person is null, remove all the text and image.
        drinkNameLabel.text = ""
        priceLabel.text     = ""
        descLabel.text      = ""
        sugarLabel.text     = ""
        commentLabel.text   = ""
        imageView.image     = null
    }
  }

  // Reset the view with (None) and get drink details on click
  showDrinkDetails(None)
  menuTable.selectionModel().selectedItem.onChange((_, _, newValue) => {
      showDrinkDetails(Option(newValue))
    }
  )

  // Goes to EditDrinkDialog with the new drink to add
  def handleAddToCart(): Unit = {
    // Gets the selected drink to be added
    val selectedDrinkOption = Option(menuTable.selectionModel().selectedItem.value)

    selectedDrinkOption match {
      case Some(selectedDrink) =>
        // New drink created to prevent changes to existing drinks
        val newDrink = new Drink(
          selectedDrink.drinkName.value,
          selectedDrink.price.value,
          selectedDrink.description.value,
          selectedDrink.sugar.value,
          selectedDrink.comment.value,
          selectedDrink.image.value.impl_getUrl()
        )
        MainApp.showEditDrinkDialog(newDrink)
        Cart.addDrink(newDrink)
      case None =>
        new Alert(AlertType.Error) {
          title = "No Drink Selected"
          headerText = "No drink was selected"
          contentText = "Please select a drink to add"
        }.showAndWait()
    }
  }

  // Goes to Cart page
  def handleShowCart(): Unit = {
    MainApp.showCart()
  }
}