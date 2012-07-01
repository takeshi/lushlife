package controllers

import play.api._
import play.api.mvc._
import model.CommonView
import common.Logging
import play.api.libs.concurrent.Akka
import play.api.Play._

object Application extends Controller {

  val commonView = CommonView(title = "Default")

  def index = Logging { req =>
    Ok(views.html.index(CommonView(title = "Your new application is ready.")))
  }

  def sample = Logging { req =>
    Ok(views.html.sample(CommonView(title = "SampleCode")))
  }

}