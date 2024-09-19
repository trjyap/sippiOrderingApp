package ty.pop.sippi.view
import ty.pop.sippi.model._

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Alert, ChoiceBox, Label, TextField}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class EditDrinkDialogController (
                                private val drinkNameLabel: Label,
                                private val descLabel: Label,
                                private val sugarChoiceBox: ChoiceBox[Int],
                                private val commentField: TextField
                                ) {
  var dialogStage: Stage = null
  private var _drink: Drink = null
  var confirmClicked = false
  sugarChoiceBox.items = ObservableBuffer(1, 2, 3, 4)


  // Sets up the drink to be edited
  def drink = _drink
  def drink_=(x: Drink) {
    _drink = x
    drinkNameLabel.text   = _drink.drinkName.value
    descLabel.text        = _drink.description.value
    sugarChoiceBox.value  = _drink.sugar.value
    commentField.text     = _drink.comment.value
  }

  // To confirm drink details
  def handleConfirm(): Unit = {
    if (isValidInput) {
      // Sugar and comment are the only values that can be edited
      _drink.sugar.value  = sugarChoiceBox.getValue
      _drink.comment      <== commentField.text

      confirmClicked = true
      dialogStage.close()
      Cart.updateTime()
    }
  }

  // Closing the dialog window
  def handleCancel(): Unit = {
    dialogStage.close()
  }

  // Checking if the field is null or empty
  def nullChecking(x: String): Boolean = {
    x == null || x.isEmpty
  }

  // Checking if input is valid
  private def isValidInput : Boolean = {
    var errorMessage = ""
    // When no errors are found
    if (errorMessage.isEmpty) {
      return true;
    } else {
      // Shows the error message
      new Alert(Alert.AlertType.Error){
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()
      return false;
    }
  }
}