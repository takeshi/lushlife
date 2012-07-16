package controllers
import play.api.mvc.Controller
import play.api.libs.iteratee.Enumerator
import play.api.libs.Comet
import play.api.libs.iteratee._
import play.api.libs.iteratee.Concurrent._
import common.Logger
import java.util.concurrent.ConcurrentHashMap

class CometController {
}

object CometController extends Controller {
  def logger = Logger[CometController]

  var seq = Seq[Channel[String]]()

  def index = LushlifeAction { req =>

    val id = req.queryString("id").head
    val onStart: Channel[String] => Unit = { channel =>
      logger.info("Add Pushee {}", id)
      seq = seq ++: Seq(channel)
      seq.map { c =>
        c.push("hi")
      }
    }

    val onError: (String, Input[String]) => Unit = { (message, input) =>
      logger.error("WebSocket Error {} {}", message, input)

    }

    val onComplete: () => Unit = { () =>
      logger.info("Remove Pusshee {}", id)
    }
    val out = Concurrent.unicast(onStart, onComplete, onError)

    Ok.stream(out &> Comet(callback = "console.log"))
  }

}