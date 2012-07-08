package common
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.global.ctx
import com.novus.salat.grater
import com.novus.salat._
import com.mongodb.MongoURI

class Mongo

object Mongo {
  val logger = Logger[Mongo]

  var hostname = "127.0.0.1"
  var port = "27017"
  var name = "test"

  var db = "db";

  lazy val mongo = {
    var hostname = System.getProperty("cloud.services.mongodb.connection.hostname")
    var port = System.getProperty("cloud.services.mongodb.connection.port")
    if (hostname == null) {
      hostname = this.hostname
    }
    if (port == null) {
      port = this.port
    }
    new com.mongodb.Mongo(hostname, port.toInt)
  }

  lazy val connection = {
    new MongoConnection(mongo);
  }

  lazy val mongoDb = {
    val db = System.getProperty("cloud.services.mongodb.connection.db")
    val username = System.getProperty("cloud.services.mongodb.connection.username")
    val password = System.getProperty("cloud.services.mongodb.connection.password")
    if (username != null) {
      val dv = connection(db)
      val authenticate = dv.authenticate(username, password);
      logger.info("login {} {}", authenticate, username + ":" + password)
      dv
    } else {
      connection(this.db)
    }
  }

  def persist[T <: AnyRef](t: T)(implicit m: Manifest[T], g: Grater[T]) = {
    def collection = mongoDb(m.erasure.getSimpleName())
    collection += g.asDBObject(t)
  }

  def findOne[T <: AnyRef](obj: MongoDBObject)(implicit m: Manifest[T], g: Grater[T]): T = {
    def collection = mongoDb(m.erasure.getSimpleName())
    def value = collection.findOne(obj)
    if (value != None) {
      g.asObject(value.get)
    } else {
      return null.asInstanceOf[T]
    }
  }

  def delete[T <: AnyRef](id: String)(implicit m: Manifest[T], g: Grater[T]) {
    def collection = mongoDb(m.erasure.getSimpleName())
    collection.remove(MongoDBObject("id" -> id))

  }

  def findOne[T <: AnyRef](id: String)(implicit m: Manifest[T], g: Grater[T]): T = {
    def obj = MongoDBObject("id" -> id);
    findOne[T](obj)
  }

}