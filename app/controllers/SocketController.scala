package controllers
import play.api.mvc.WebSocket
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Iteratee
import common.Logger
import java.util.concurrent.ConcurrentHashMap
import play.api.libs.iteratee.Input
import scala.util.parsing.json.JSON
import play.api.mvc.Controller
import java.util.concurrent.Executors
import play.api.libs.concurrent.Akka
import akka.actor.Actor
import play.api.Play.current
import akka.actor.Props
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Concurrent._
import play.api.libs.iteratee.Enumerator._

class SocketController {

}
object SocketController extends Controller {
  val logger = Logger[SocketController]

  val map = new ConcurrentHashMap[String, Channel[String]]

  case class PushMessage(id: String, message: String)

  class MessageActor extends Actor {
    override def receive = {
      case e: PushMessage => pushMessage(e.id, e.message)
      case e: Any => logger.error("unknown input type", e)
    }
  }

  val messageActor = Akka.system.actorOf(Props[MessageActor], name = "messageActor")

  def pushMessage = LushlifeAction { request =>
    request.body.asFormUrlEncoded.map { params =>
      messageActor ! PushMessage(
        params.get("id").get.head,
        params.get("message").get.head)
    }
    Ok("success")
  }

  def pushMessage(id: String, message: String) {
    val pushee = map.get(id)
    if (pushee == null) {
      logger.info("Removed Socket {}", id)
      return
    }
    logger.info("Push Message {}", message)
    pushee.push(message)

  }

  def sample() {
    val onStart: Channel[String] => Unit = { channel =>
      channel.push("Hello")
      channel.push("World")
      channel.push("Play20 WebSocket")
    }

    val onError: (String, Input[String]) => Unit = { (message, input) =>
      println("onError " + message + " " + input)
    }

    val onComplete: () => Unit = { () =>
      println("onComplete")
    }
    val out = Concurrent.unicast(onStart, onComplete, onError)
    out
  }

  def index = WebSocket.using[String] { requestHandler =>
    logger.info("WebSocket Create {}", requestHandler)
    val id = requestHandler.queryString("id").head
    val in = Iteratee.foreach[String] { input =>
      logger.info("Receive Message {}", input)
      pushMessage(id, input)
    }

    val onStart: Channel[String] => Unit = { channel =>
      logger.info("Add Pushee {}", id)
      map.put(id, channel)
    }

    val onError: (String, Input[String]) => Unit = { (message, input) =>
      logger.error("WebSocket Error {} {}", message, input)

    }

    val onComplete: () => Unit = { () =>
      logger.info("Remove Pusshee {}", id)
      map.remove(id)
    }
    val out = Concurrent.unicast(onStart, onComplete, onError)

    (in, out)
  }
}