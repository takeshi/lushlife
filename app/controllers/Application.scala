package controllers

import play.api._
import play.api.mvc._
import model.CommonView
import common.Logging
import play.api.libs.concurrent.Akka
import play.api.Play._

object Application extends Controller {

  def index = Logging { req =>
    Logger.info(System.getProperties().toString())
    Ok(views.html.index(new CommonView()))
  }

  def sample = Logging { req =>
    Ok(views.html.sample(new CommonView()))
  }

}