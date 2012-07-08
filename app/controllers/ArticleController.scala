package controllers

import common.Injector
import common.Logging
import common.Mongo
import model.Article.g
import model.Article.validator
import model.Article
import model.CommonView
import model.Validator
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.api.Logger

object ArticleController extends Controller {

  val $ = Injector

  def read(id: String) = Logging { req =>
    Logger.info("connection " + Mongo.connection.toString())
    Async {
      Logging.future(req) {
        val c = CommonView(req)
        val article = Mongo.findOne[Article](id)
        if (article == null) {
          if (c.logined) {
            Ok(views.html.editArticle(Article.create(id), c))
          } else {
            Redirect("/login")
          }
        } else {
          Ok(views.html.article(article, c))
        }
      }
    }
  }

  object Api {

    def delete(id: String) = Logging { req =>
      val c = CommonView(req)
      if (c.logined == false) {
        Unauthorized
      } else {
        Async {
          Logging.future(req) {
            Mongo.delete[Article](id)
            Ok(Json.toJson(Map("message" -> "delete success", "content" -> views.html.div.editArticle(Article.create(id)).toString)))
          }
        }
      }
    }

    def edit(id: String) = Logging { req =>
      val c = CommonView(req)
      if (c.logined == false) {
        Unauthorized
      } else {
        Async {
          Logging.future(req) {
            var article = Mongo.findOne[Article](id)
            if (article == null) {
              article = Article.create(id)
            }
            Ok(Json.toJson(Map("message" -> "delete success", "content" -> views.html.div.editArticle(article).toString)))
          }
        }
      }
    }

    def persist = Logging { req =>
      val c = CommonView(req)
      if (c.logined == false) {
        Unauthorized
      } else {
        req.body.asJson.map { json =>
          val article = Article.fromJSON(json.toString)
          val errors = Validator.validate(article)
          if (errors.length > 0) {
            Forbidden(Json.toJson(Map("errors" -> errors)))
          } else {
            Async {
              Logging.future(req) {
                Mongo.persist[Article](article)
                Ok(Json.toJson(Map("message" -> "persist success", "content" -> views.html.div.article(article, c).toString)))
              }
            }
          }
        }.getOrElse {
          BadRequest(Json.toJson(Map("message" -> "BadRequestBody", "body" -> req.body.toString())))
        }
      }
    }
  }
}