package controllers

import play.api._
import play.api.mvc._
import model.CommonView
import play.api.libs.concurrent.Akka
import play.api.Play._
import model.Blogger
import java.security.MessageDigest
import model.Blogger
import com.mongodb.casbah.Imports._
import common.RedisClientManager
import java.util.UUID
import common.Logger
import common.Lushlife._
import common.Auth._
import common.Auth

class LoginController {

}
object LoginController extends Controller {
  def logger = Logger[LoginController]

  def index = LushlifeAction { req =>
    // Httpsへリダイレクト
    if (isCloud && req.cookies.get("lushlife") == None) {
      val cookie = Cookie(
        name = "lushlife",
        value = "lushlife",
        secure = true,
        httpOnly = true)
      Redirect("https://" + req.headers("HOST") + "/login").withCookies(cookie)
    } else {
      var url = req.getQueryString("url").getOrElse { "/" }
      Ok(views.html.login(Blogger.create(), url, CommonView(req)))
    }
  }
  def logout = LushlifeAction { req =>
    req.body.asFormUrlEncoded.map { form =>
      var url = form.get("url").getOrElse(Seq("/")).head
      Auth.blogger(req).map { blogger =>
        Redirect(url).withCookies(Auth.logout(req))
      } getOrElse {
        Redirect("/login?ur=l" + url)
      }
    } getOrElse {
      Redirect("/login")
    }
  }
  def tryLogin = LushlifeAction { req =>
    req.body.asFormUrlEncoded.map[Result] { form =>
      val email = form.get("email").get.head
      val password = md5SumString(form.get("password").get.head)
      val url = form.get("url").getOrElse(List("/")).head

      if (Blogger.collection.size == 0) {
        val blogger = new Blogger(new ObjectId, email, password)
        Blogger.collection += Blogger.toDBObject(blogger)
        Redirect(url).withCookies(Auth.login(req, blogger))
      } else {
        val result = Blogger.findOne(MongoDBObject("email" -> email, "password" -> password))
        result.map { blogger =>
          logger.info("login success {}", email)
          Redirect(url).withCookies(Auth.login(req, result.get))
        }.getOrElse {
          logger.info("login failed {}", email)
          Ok(views.html.login(Blogger(null, email, ""), url, CommonView(req)))
        }
      }
    }.getOrElse {
      BadRequest("bad request " + req.body)
    }
  }
}