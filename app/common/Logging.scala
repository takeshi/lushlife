package common
import play.api.libs.concurrent.Promise
import play.api.libs.concurrent.Akka
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.Application

class Logging

object Logging {
  def logger = Logger[Logging]

  def apply(f: Request[Any] => Result): Action[AnyContent] = {
    Action { request =>
      logger.info("IN  {}" , request)
      var result = f(request)
      logger.info("OUT {}" , result)
      result
    }
  }

  def future[T](r: Request[Any])(f: => T)(implicit application: Application): Promise[T] = {
    def ff = {
      logger.info("AIN  {}" , r)
      val result = f
      logger.info("AOUT {}" , result)
      result
    }
    Akka.future(ff)(application);
  }
}