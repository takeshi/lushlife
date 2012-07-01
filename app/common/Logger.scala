package common
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Logger {
  def apply[T](implicit m: Manifest[T]): Logger = {
    LoggerFactory.getLogger(m.erasure).asInstanceOf[Logger]
  }
}