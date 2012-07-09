package controllers

import play.api._
import play.api.mvc._
import model.CommonView
import common.Logging
import play.api.libs.concurrent.Akka
import play.api.Play._
import common.RedisClientManager

object Application extends Controller {

  def index = Logging { req =>
    if (LoginController.isCloud) {
      Redirect("/login")
    } else {
      //      val client = RedisClientManager.client
      //      client.set("hi", "hello")
      //      val v = client.get("hi")
      //      Logger.info(v.toString)
      Ok(views.html.index(CommonView(req)))
    }

  }

  def sample = Logging { req =>
    Logger.info(System.getProperties().toString())
    if (LoginController.isCloud) {
      Redirect("/login")
    } else {
      Ok(views.html.sample(CommonView(req)))
    }
  }

}