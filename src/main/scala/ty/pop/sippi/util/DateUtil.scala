package ty.pop.sippi.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

object DateUtil {
  val dateFormatter =  DateTimeFormatter.ofPattern("dd-LL-yyyy, HH:mm:ss")

  implicit class DateFormatter (val date : LocalDate){
    def asString : String = {
      if (date == null) {
        return null
      }
      dateFormatter.format(date)
    }
  }

  implicit class StringFormatter (val data : String) {
    def parseLocalDate : LocalDate = {
      try {
        LocalDate.parse(data, dateFormatter)
      } catch {
        case  e: DateTimeParseException => null
      }
    }

    def isValid : Boolean = {
      data.parseLocalDate != null
    }
  }
}
