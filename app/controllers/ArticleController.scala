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

object ArticleController extends Controller {

  val $ = Injector

  def read(id: String) = LushlifeAction { req =>
    val c = CommonView(req)
    val article = Mongo.findOne[Article](id)
    if (article == null) {
      if (c.logined) {
        Ok(views.html.editArticle(Article.create(id), c))
      } else {
        Redirect("/login")
      }
    } else {
      if (article.open) {
        Ok(views.html.article(article, c))
      } else {
        Redirect("/login")
      }
    }
  }

  object Api {

    def preview = LushlifeAction { req =>
      val c = CommonView(req)
      req.body.asJson.map { json =>
        val article = Article.fromJSON(json.toString)
        Ok(Json.toJson(Map("content" -> views.html.sub.articlePrevice(article, c).toString)))
      }.getOrElse {
        BadRequest
      }
    }

    def delete(id: String) = LushlifeAction.RequiredAuth { req =>
      val c = CommonView(req)
      Mongo.delete[Article](id)
      Ok(Json.toJson(Map("message" -> "delete success", "content" -> views.html.div.editArticle(Article.create(id)).toString)))
    }

    def edit(id: String) = LushlifeAction.RequiredAuth { req =>
      val c = CommonView(req)
      var article = Mongo.findOne[Article](id)
      if (article == null) {
        article = Article.create(id)
      }
      Ok(Json.toJson(Map("message" -> "delete success", "content" -> views.html.div.editArticle(article).toString)))
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