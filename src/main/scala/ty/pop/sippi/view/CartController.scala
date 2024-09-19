package ty.pop.sippi.view
import ty.pop.sippi.model._
import ty.pop.sippi.MainApp
import ty.pop.sippi.util.DateUtil

import scalafx.beans.property.ObjectProperty
import javafx.scene.control.TableCell
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, ButtonType, Label, TableColumn, TableView}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.TableColumn.CellDataFeatures
import java.time.LocalDateTime
import scalafxml.core.macros.sfxml

@sfxml
class CartController(
                    private val cartTable: TableView[Drink],
                    private val numberColumn: TableColumn[Drink, Int],
                    private val drinkNameColumn: TableColumn[Drink, String],
                    private val sugarColumn: TableColumn[Drink, Int],
                    private val commentColumn: TableColumn[Drink, String],
                    private val priceColumn: TableColumn[Drink, Double],
                    private val totalPriceLabel: Label,
                    private val updateTimeLabel: Label
                    ) {
  cartTable.items = Cart.cartItems
  // Configure index column
  numberColumn.cellValueFactory = { cellData: CellDataFeatures[Drink, Int] =>
    ObjectProperty((cartTable.getItems.indexOf(cellData.getValue) + 1))
  }
  numberColumn.cellFactory = { _ =>
    new TableCell[Drink, Int] {
      override def updateItem(item: Int, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null) {
          setText(null)
        } else {
          setText(item.toString)
        }
      }
    }
  }

  // Configure other columns
  drinkNameColumn.cellValueFactory = {_.value.drinkName}
  sugarColumn.cellValueFactory = {_.value.sugar}
  commentColumn.cellValueFactory = {_.value.comment}
  priceColumn.cellValueFactory = {_.value.price}

  // Configure total price and update time label
  changeTotal()
  changeTime()

  // Change total price label when cart changes are made
  def changeTotal(): Unit = {
    totalPriceLabel.text <== ObjectProperty(Cart.calculateTotalPrice().toString)
  }

  // Change update time label when cart changes are made
  def changeTime(): Unit = {
    updateTimeLabel.text = LocalDateTime.now.format(DateUtil.dateFormatter)
  }

  // For when Add button or menubar item is clicked
  def handleAddDrink(): Unit = {
    MainApp.showMenu()
  }

  // For when Edit button or menubar item is clicked
  def handleEditDrink(): Unit = {
    // Gets the selected drink to be edited
    val selectedDrinkOption = Option(cartTable.selectionModel().selectedItem.value)

    selectedDrinkOption match {
      case Some(selectedDrink) =>
        MainApp.showEditDrinkDialog(selectedDrink)
        changeTime()
      case None =>
        new Alert(AlertType.Error) {
          title = "No Drink Selected"
          headerText = "No drink was selected"
          contentText = "Please select a drink from the cart to edit"
        }.showAndWait()
    }
  }

  def handleRemoveDrink(action: ActionEvent): Unit = {
    val selectedDrink = cartTable.selectionModel().selectedItem.value
    if (selectedDrink != null) {
      val removePopUp = new Alert(AlertType.Confirmation) {
        initOwner(MainApp.stage)
        title = "Remove Drink"
        headerText = ""
        contentText = s"Are you sure you want to remove ${selectedDrink.drinkName.value}?"
      }.showAndWait()
      removePopUp match {
        case Some(ButtonType.OK) =>
          Cart.removeDrink(selectedDrink)
          Cart.updateTime()
          changeTotal()
          changeTime()
        case _ =>
          // Do nothing
      }
    } else {
      // When no drink was selected from the cart
      new Alert(AlertType.Error) {
        initOwner(MainApp.stage)
        title = "No Drink Selected"
        headerText = "No drink was selected"
        contentText = "Please select a drink from the cart to remove"
      }.showAndWait()
    }
  }

  def handleCompleteOrder(action: ActionEvent): Unit = {
    if (Cart.cartItems.isEmpty) { // Catch if cart is empty
      new Alert(AlertType.Error) {
        initOwner(MainApp.stage)
        title = "Cart Empty"
        headerText = "Cart is empty"
        contentText = "Please add some items from the menu before placing order"
      }.showAndWait()
    } else {
      val confirmSubmit = new Alert(AlertType.Confirmation) {
        initOwner(MainApp.stage)
        title = "Confirm Submit Order?"
        headerText = "Is Your Order Complete?"
        contentText = "Click OK to submit your order"
      }.showAndWait
      // User has to confirm they are ready to submit order
      confirmSubmit match {
        case Some(ButtonType.OK) =>
          new Alert(AlertType.Information) {
            initOwner(MainApp.stage)
            title = "Order Placed"
            headerText = "Your order has been placed"
            contentText = s"Please proceed to counter to pay RM${totalPriceLabel.text.value} \n" +
              "Let our staff know if there are any issues\n" +
              s"Last updated: ${updateTimeLabel.text.value}"
          }.showAndWait()
          // Order should now be saved to the database
          Cart.cartItems.foreach(drink => Cart.save(drink))
        case _ =>
          // Order is not confirmed, do nothing
      }
    }
  }
}