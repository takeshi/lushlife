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
import common.Lushlife

object MainController extends Controller {

  def index = LushlifeAction { req =>
    def c = CommonView(req)

    // ログインしていたら全部表示する
    val cursol = if (c.logined) {
      Mongo.mongoDb("Article").find()
    } else {
      Mongo.mongoDb("Article").find(MongoDBObject({ "open" -> true }))
    }

    val articles = cursol.sort(MongoDBObject({
      "updateTime" -> -1
    })).limit(10).map {
      dbObject =>
        Article.toObject(dbObject).asInstanceOf[Article]
    }
    Ok(views.html.index(articles.toList, c))

  }

  def sample = LushlifeAction { req =>
    Logger.info(System.getProperties().toString())
    if (Lushlife.isCloud) {
      Redirect("/login")
    } else {
      Ok(views.html.sample(CommonView(req)))
    }
  }

}