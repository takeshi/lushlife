package common
import com.redis.RedisClientPool
import com.redis.RedisClient

class RedisClientManager {
}

object RedisClientManager {
  val logger = Logger[RedisClientManager]

  var host = "localhost"
  var port = "6379"
  var password: String = _

  def client[T](body: RedisClient => T) = {
    val client = _client
    client.withClient { c =>
      if (c.disconnect) {
        c.connect
        if (password != null) {
          val auth = c.auth(password)
          logger.info("login redis {}", auth)
        }
      }
      body(c)
    }
  }

  lazy val _client = {
    var host = System.getProperty("cloud.services.redis.connection.host")
    var port = System.getProperty("cloud.services.redis.connection.port")
    if (host == null) {
      host = this.host
    }
    if (port == null) {
      port = this.port
    }
    val client = new RedisClientPool(host, port.toInt)
    password = System.getProperty("cloud.services.redis.connection.password")
    //    if (password != null) {
    //      val auth = client.auth(password)
    //      logger.info("login redis {}", auth)
    //    }
    client
  }

}