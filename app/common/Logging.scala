package common
import play.api.libs.concurrent.Promise
import play.api.libs.concurrent.Akka
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.Application
import play.api.Logger
import play.api.mvc.SimpleResult
import play.api.mvc.SimpleResult

object Logging {
  def apply(f: Request[Any] => Result): Action[AnyContent] = {
    Action { request =>
      Logger.info("IN  " + request.toString())
      var result = f(request)
      Logger.info("OUT " + result.toString())
      result
    }
  }

  def future[T](r: Request[Any])(f: => T)(implicit application: Application): Promise[T] = {
    def ff = {
      Logger.info("AIN  " + r.toString())
      val result = f
      Logger.info("AOUT " + result.toString())
      result
    }
    Akka.future(ff)(application);
  }
}