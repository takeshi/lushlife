package controllers

import play.api._
import play.api.mvc._
import model.CommonView
import play.api.libs.concurrent.Akka
import play.api.Play._
import common.RedisClientManager
import model.Article
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import common.Mongo

object MainController extends Controller {

  def index = LushlifeAction { req =>
    if (LoginController.isCloud) {
      Redirect("/login")
    } else {
      val cursol = Mongo.mongoDb("Article").find().sort(
        MongoDBObject({ "updateTime" -> -1 })).limit(10)
      val articles = cursol.map { dbObject =>
        Article.toObject(dbObject).asInstanceOf[Article]
      }
      Ok(views.html.index(articles.toList, CommonView(req)))
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