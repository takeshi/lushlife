package common
import com.redis.RedisClientPool

object RedisClientManager {
  var host = "localhost"
  var port = 6379

  lazy val clientPool = {
    new RedisClientPool(host, port)
  }

}