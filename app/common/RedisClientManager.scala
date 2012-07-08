package common
import com.redis.RedisClientPool
import com.redis.RedisClient

class RedisClientManager {
}

object RedisClientManager {
  val logger = Logger[RedisClientManager]

  var host = "localhost"
  var port = "6379"

  lazy val client = {
    var host = System.getProperty("cloud.services.redis.connection.host")
    var port = System.getProperty("cloud.services.redis.connection.port")
    if (host == null) {
      host = this.host
    }
    if (port == null) {
      port = this.port
    }
    val client = new RedisClient(host, port.toInt)
    var password = System.getProperty("cloud.services.redis.connection.password")
    if (password != null) {
      val auth = client.auth(password)
      logger.info("login redis {}", auth)
    }
    client
  }

}