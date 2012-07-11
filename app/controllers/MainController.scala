package controllers

import play.api._
import play.api.mvc._
import model.CommonView
import play.api.libs.concurrent.Akka
import play.api.Play._
import common.RedisClientManager

object MainController extends Controller {

  def index = LushlifeAction { req =>
    if (LoginController.isCloud) {
      Redirect("/login")
    } else {
      Ok(views.html.index(CommonView(req)))
    }

  }

  def sample = LushlifeAction { req =>
    Logger.info(System.getProperties().toString())
    if (LoginController.isCloud) {
      Redirect("/login")
    } else {
      Ok(views.html.sample(CommonView(req)))
    }
  }

}