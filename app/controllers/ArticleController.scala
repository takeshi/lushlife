package controllers

import common.Injector
import common.Mongo
import model.Article.g
import model.Article.validator
import model.Article
import model.CommonView
import common.Validator
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.Logger
import java.util.Date
import model.Blogger
import com.mongodb.casbah.commons.MongoDBObject

object ArticleController extends Controller {

  val $ = Injector

  object View {
    def read(name: String, id: String) = LushlifeAction { req =>
      val c = CommonView(req)
      Blogger.findOne(MongoDBObject("twitterName" -> name)).map { blogger =>
        Article.findOne(MongoDBObject("owner" -> blogger._id.toString, "id" -> id)).map { article =>
          c.title = "Lushlife | " + article.title
          if (c.logined)
            Ok(views.html.article(article, c))
          else if (article.open == false)
            Redirect("/")
          else
            Ok(views.html.article(article, c))
        }.getOrElse {
          if (c.logined && name == blogger.twitterName)
            Ok(views.html.editArticle(Article.create(id), c))
          else
            Redirect("/?ArticleNotFound:")
        }
      }.getOrElse {
        Redirect("/?BloggerNotFound")
      }
    }
  }

  object Rerender {

    def preview = LushlifeAction { req =>
      val c = CommonView(req)
      req.body.asJson.map { json =>
        val article = Article.fromJSON(json.toString)
        Ok(Json.toJson(Map("content" -> views.html.sub.articlePrevice(article, c).toString)))
      }.getOrElse {
        BadRequest
      }
    }

    def delete(name: String, id: String) = LushlifeAction.RequiredAuth { req =>
      val c = CommonView(req)
      Mongo.delete[Article](id)
      Ok(Json.toJson(Map("message" -> "delete success", "content" -> views.html.div.editArticle(Article.create(id), c).toString)))
    }

    def edit(id: String) = LushlifeAction.RequiredAuth { req =>
      val c = CommonView(req)
      var article = Mongo.findOne[Article](id)
      if (article == null) {
        article = Article.create(id)
      }
      Ok(Json.toJson(Map("message" -> "delete success", "content" -> views.html.div.editArticle(article, c).toString)))
    }

    def persist = LushlifeAction.RequiredAuth { req =>
      val c = CommonView(req)
      req.body.asJson.map { json =>
        val article = Article.fromJSON(json.toString)
        val errors = Validator.validate(article)
        if (errors.length > 0) {
          Forbidden(Json.toJson(Map("errors" -> errors)))
        } else {
          Mongo.persist[Article](article)
          Ok(Json.toJson(Map("message" -> "persist success", "content" -> views.html.div.article(article, c).toString)))
        }
      }.getOrElse {
        BadRequest(Json.toJson(Map("message" -> "BadRequestBody", "body" -> req.body.toString())))
      }
    }

  }
}