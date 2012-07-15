package common
import play.api.mvc.Request
import play.api.mvc.AnyContent
import java.security.MessageDigest
import model.Blogger
import org.bson.types.ObjectId
class Auth {}

object Auth {
  def logger = Logger[Auth]
  def AUTH_KEY = "authkey"

  def blogger(req: Request[AnyContent]): Option[Blogger] = {
    logined(req).map { oid =>
      if (oid.isEmpty()) {
        None
      } else {
        Blogger.findOneById(new ObjectId(oid))
      }
    }.getOrElse {
      logger.info("not logined {}", req)
      None
    }
  }

  def logined(req: Request[AnyContent]): Option[String] = {
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

  def updateExpire(key: String, oid: String) {
    // ログイン情報は24時間で消える
    RedisClientManager.client { _.setex(key, 24 * 60 * 60, oid) }
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