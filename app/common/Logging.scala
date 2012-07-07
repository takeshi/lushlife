package common
import play.api.libs.concurrent.Promise
import play.api.libs.concurrent.Akka
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.Application
import java.util.ArrayList

class Logging

object Logging {
  def logger = Logger[Logging]

  def apply(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      logger.info("IN  {}", request)
      var result = f(request)
      logger.info("OUT {}", result)
      result
    }
  }

  def future[T](r: Any)(f: => T)(implicit application: Application): Promise[T] = {
    def ff = {
      try {
        logger.info("AIN  {}", r)
        val result = f
        logger.info("AOUT {}", result)
        result
      } catch {
        case e: Throwable =>
          logger.info("AOUTE {}", e)
          throw e;
      }
    }
    Akka.future(ff)(application);
  }
}