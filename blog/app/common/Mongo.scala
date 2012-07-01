package common
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.novus.salat.global.ctx
import com.novus.salat.grater
import com.novus.salat._
import play.api.Logger

object Mongo {

  var server = "127.0.0.1"
  var port = 27017
  var name = "test"

  val defaultDbName = "pf2";

  lazy val mongo = {
    Logger.info("create Mongo server:" + server + " port:" + port)
    new com.mongodb.Mongo(server, port)
  }

  lazy val connection = {
    def con = new MongoConnection(mongo);
    con.apply(defaultDbName).dropDatabase()
    con
  }

  lazy val mongoDb = {
    connection(defaultDbName)
  }

  def persist[T <: AnyRef](t: T)(implicit m: Manifest[T], g: Grater[T]) = {
    def collection = mongoDb(m.erasure.getSimpleName())
    collection += g.asDBObject(t)
  }

  def findOne[T <: AnyRef](obj: MongoDBObject)(implicit m: Manifest[T], g: Grater[T]): T = {
    def collection = mongoDb(m.erasure.getSimpleName())
    def value = collection.findOne(obj)
    g.asObject(value.get)
  }

}