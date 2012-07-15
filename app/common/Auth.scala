package common
import play.api.mvc.Request
import play.api.mvc.AnyContent
import java.security.MessageDigest
import model.Blogger
import org.bson.types.ObjectId
import play.api.mvc.Cookie
import java.util.UUID
import play.api.mvc.Controller
import play.api.mvc.Cookie
class Auth {}

object Auth {
  def logger = Logger[Auth]
  def AUTH_KEY = "authkey"

  def authKey(req: Request[AnyContent]): Option[Cookie] = {
    req.cookies.get(AUTH_KEY)
  }

  def blogger(req: Request[AnyContent]): Option[Blogger] = {
    logined(req).map { oid =>
      if (oid.isEmpty()) {
        None
      } else {
        Some {
          Blogger.findOneById(new ObjectId(oid)).get
        }
      }
    }.getOrElse {
      logger.info("not logined {}", req)
      None
    }
  }

  private def logined(req: Request[AnyContent]): Option[String] = {
    val key = req.cookies.get(AUTH_KEY)
    if (key == None) {
      None
    } else {
      val auth = RedisClientManager.client { _.get(key.get.value) }
      if (auth == None) {
        None
      } else {
        if (auth.get == "") {
          None
        } else {
          updateExpire(key.get.value, auth.get)
          Some(auth.get)
        }
      }
    }
  }
  def login(req: Request[AnyContent], blogger: Blogger): Cookie = {
    val key = UUID.randomUUID().toString();
    val cookie = Cookie(
      name = AUTH_KEY,
      value = key,
      secure = Lushlife.isCloud,
      httpOnly = true)
    Auth.updateExpire(key, blogger._id.toString)
    cookie
  }

  def logout(req: Request[AnyContent]): Cookie = {
    val key = req.cookies.get(AUTH_KEY).get.value
    Auth.removeAuthKey(key)
    Cookie(
      name = AUTH_KEY,
      value = "",
      secure = false,
      httpOnly = true)
  }

  def updateExpire(key: String, oid: String) {
    // ログイン情報は24時間で消える
    RedisClientManager.client { _.setex(key, RedisClientManager.timeout, oid) }
  }

  def removeAuthKey(key: String) {
    // ログイン情報は24時間で消える
    RedisClientManager.client { _.setex(key, 1, false) }
  }

  def md5SumString(str: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(str.getBytes())
    md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
  }

}