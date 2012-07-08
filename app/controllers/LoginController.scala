package controllers

import play.api._
import play.api.mvc._
import model.CommonView
import common.Logging
import play.api.libs.concurrent.Akka
import play.api.Play._
import model.Blogger
import java.security.MessageDigest
import model.Blogger
import com.mongodb.casbah.Imports._
import common.RedisClientManager
import java.util.UUID

object LoginController extends Controller {

  def AUTH_KEY = "authkey"

  def index = Logging { req =>
    // Httpsへリダイレクト
    if (isCloud && req.cookies.get("lushlife") == None) {
      val cookie = Cookie(
        name = "lushlife",
        value = "lushlife",
        secure = true,
        httpOnly = true)
      Redirect("https://" + req.headers("HOST") + "/login").withCookies(cookie)
    } else {
      Ok(views.html.login(Blogger.create(), CommonView(req)))
    }
  }

  def logined(req: Request[AnyContent]): Boolean = {
    val key = req.cookies.get(AUTH_KEY)
    if (key == None) {
      false
    } else {
      val auth = RedisClientManager.client.get(key.get.value)
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
    RedisClientManager.client.setex(key, 24 * 60 * 60, true)
  }

  private def remove(key: String) {
    // ログイン情報は24時間で消える
    RedisClientManager.client.setex(key, 1, false)
  }

  private def login(req: Request[AnyContent]): Result = {
    val key = UUID.randomUUID().toString();
    val cookie = Cookie(
      name = AUTH_KEY,
      value = key,
      secure = isCloud,
      httpOnly = true)
    updateEx(key)
    Redirect("/").withCookies(cookie)
  }

  def logout = Logging { req =>
    // Httpsへリダイレクト
    if (!logined(req)) {
      Redirect("/login")
    } else {
      val key = req.cookies.get(AUTH_KEY).get.value
      remove(key)
      val cookie = Cookie(
        name = AUTH_KEY,
        value = "",
        secure = false,
        httpOnly = true)
      Redirect("/login").withCookies(cookie)
    }
  }
  def tryLogin = Logging { req =>
    req.body.asFormUrlEncoded.map[Result] { form =>
      val email = form.get("email").get.head
      val password = md5SumString(form.get("password").get.head)

      if (Blogger.collection.size == 0) {
        val blogger = new Blogger(new ObjectId, email, password)
        Blogger.collection += Blogger.toDBObject(blogger)
        login(req)
      } else {
        val blogger = new Blogger(null, email, password)
        val result = Blogger.findOne(Blogger.toDBObject(blogger))
        if (result == None) {
          Ok(views.html.login(Blogger(null, email, ""), CommonView(req)))
        } else {
          login(req)
        }
      }
    }.getOrElse {
      BadRequest("")
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