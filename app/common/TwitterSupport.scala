package common
import play.api.mvc.Request
import model.TwitterUser
import play.api.mvc.AnyContent
import twitter4j.auth.RequestToken

class TwitterSupport {

}

object TwitterSupport {

  def read(req: Request[AnyContent]): Option[TwitterUser] = {
    Auth.authKey(req).map { cookie =>
      RedisClientManager.client { client =>
        val key = cookie.value
        val name = client.get(key + "-name")
        val accessToken = client.get(key + "-accessToken")
        val accessTokenSecret = client.get(key + "-accessTokenSecret")
        if (name == None || accessToken == None || accessTokenSecret == None) {
          None
        } else {
          Some(TwitterUser(name.get, accessToken.get, accessTokenSecret.get))
        }
      }
    }.getOrElse {
      None
    }
  }

  def write(req: Request[AnyContent], twitterUser: TwitterUser) {
    Auth.authKey(req).map { cookie =>
      val key = cookie.value
      RedisClientManager.client { client =>
        client.setex(key + "-name", RedisClientManager.timeout, twitterUser.name)
        client.setex(key + "-accessToken", RedisClientManager.timeout, twitterUser.accessToken)
        client.setex(key + "-accessTokenSecret", RedisClientManager.timeout, twitterUser.accessTokenSecret)
      }
    }
  }

  def requestTokenSecret(req: Request[AnyContent], requestToken: String): Option[String] = {
    RedisClientManager.client(_.get("twitter-" + requestToken))
  }

  def requestTokenSecret(req: Request[AnyContent], requestToken: RequestToken) = {
    RedisClientManager.client(
      _.setex("twitter-" + requestToken.getToken(),
        RedisClientManager.timeout,
        requestToken.getTokenSecret()))
  }

}