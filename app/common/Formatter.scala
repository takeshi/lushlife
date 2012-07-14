package common
import java.util.Date
import java.text.SimpleDateFormat

object Formatter {
  def date(date: Date): String = {
    def formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    formatter.format(date)
  }
}