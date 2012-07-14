package common
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object Formatter {
  def date(date: Date): String = {
    val formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    formatter.setTimeZone(TimeZone.getTimeZone("JST"))
    formatter.format(date)
  }

}