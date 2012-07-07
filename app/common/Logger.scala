package common
import org.slf4j.LoggerFactory

object Logger {
  def apply[T](implicit m: Manifest[T]): org.slf4j.Logger = {
    LoggerFactory.getLogger(m.erasure).asInstanceOf[org.slf4j.Logger]
  }
}