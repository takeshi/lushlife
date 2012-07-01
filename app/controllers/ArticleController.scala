package controllers

import common.Service
import common.Injector
import common.Logging
import model.CommonView
import play.api.Play.current
import play.api.mvc.Controller
import service.ArticleService
import model.Article

object ArticleController extends Controller {

  def read(id: String) = Logging { req =>
    Async {
      Logging.future(req) {
        Injector[ArticleService].persist(Article(0, id))
        Ok(views.html.article(CommonView(title = id)))
      }
    }
  }
}