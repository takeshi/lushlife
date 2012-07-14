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

class LoginController {

}
object LoginController extends Controller {
  def logger = Logger[LoginController]
  def AUTH_KEY = "authkey"

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

  def logined(req: Request[AnyContent]): Boolean = {
    val key = req.cookies.get(AUTH_KEY)
    if (key == None) {
      false
    } else {
      val auth = RedisClientManager.client { _.get(key.get.value) }
      if (auth == None) {
        false
      } else {
        if (auth.get != "true") {
          false
        } else {
          updateEx(key.get.value)
          true
        }
      }
    }
  }

  private def updateEx(key: String) {
    // ログイン情報は24時間で消える
    RedisClientManager.client { _.setex(key, 24 * 60 * 60, true) }
  }

  private def remove(key: String) {
    // ログイン情報は24時間で消える
    RedisClientManager.client { _.setex(key, 1, false) }
  }

  private def login(req: Request[AnyContent], url: String): Result = {
    val key = UUID.randomUUID().toString();
    val cookie = Cookie(
      name = AUTH_KEY,
      value = key,
      secure = isCloud,
      httpOnly = true)
    updateEx(key)
    logger.info("Redirecting {}", url)
    Redirect(url).withCookies(cookie)
  }

  def logout = LushlifeAction { req =>
    // Httpsへリダイレクト
    req.body.asFormUrlEncoded.map { form =>
      var url = form.get("url").getOrElse(Seq("/")).head
      if (!logined(req)) {
        Redirect("/login?ur=l" + url)
      } else {
        val key = req.cookies.get(AUTH_KEY).get.value
        remove(key)
        val cookie = Cookie(
          name = AUTH_KEY,
          value = "",
          secure = false,
          httpOnly = true)
        Redirect("/login?url=" + url).withCookies(cookie)
      }
    } getOrElse {
      Redirect("/login")
    }
  }
  def tryLogin = LushlifeAction { req =>
    req.body.asFormUrlEncoded.map[Result] { form =>
      val email = form.get("email").get.head
      val password = md5SumString(form.get("password").get.head)
      val url = form.get("url").get.head

      if (Blogger.collection.size == 0) {
        val blogger = new Blogger(new ObjectId, email, password)
        Blogger.collection += Blogger.toDBObject(blogger)
        login(req, url)
      } else {
        val blogger = new Blogger(null, email, password)
        val result = Blogger.findOne(Blogger.toDBObject(blogger))
        if (result == None) {
          logger.info("login failed {}", email)
          Ok(views.html.login(Blogger(null, email, ""), url, CommonView(req)))
        } else {
          logger.info("login success {}", email)
          login(req, url)
        }
      }
    }.getOrElse {
      BadRequest("bad request " + req.body)
    }
  }

  def md5SumString(str: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(str.getBytes())
    md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
  }

  lazy val isCloud: Boolean = {
    System.getProperty("cloud.provider.url") != null
  }

}