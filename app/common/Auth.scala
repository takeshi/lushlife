package common
import play.api.mvc.Request
import play.api.mvc.AnyContent
import java.security.MessageDigest

object Auth {
  def AUTH_KEY = "authkey"

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
          updateExpire(key.get.value)
          true
        }
      }
    }
  }

  def updateExpire(key: String) {
    // ログイン情報は24時間で消える
    RedisClientManager.client { _.setex(key, 24 * 60 * 60, true) }
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