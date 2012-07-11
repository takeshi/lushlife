package controllers
import play.api.libs.concurrent.Promise
import play.api.libs.concurrent.Akka
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.Application
import java.util.ArrayList
import play.api.mvc.Controller
import play.api.Play.current
import common.Logger
import model.CommonView

class LushlifeAction

object LushlifeAction extends Controller {
  def logger = Logger[LushlifeAction]

  object RequiredAuth {
    def apply(f: Request[AnyContent] => Result): Action[AnyContent] = {
      LushlifeAction { request =>
        val c = CommonView(request)
        if (c.logined == false) {
          Unauthorized
        } else {
          f(request)
        }
      }
    }
  }

  def apply(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      Async {
        logger.info("IN  {}", request)
        var result = future(request) {
          f(request)
        }
        logger.info("OUT {}", result)
        result
      }
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