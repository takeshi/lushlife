package model

import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import org.scala_tools.time.Imports._
import com.novus.salat.dao.{ SalatDAO, ModelCompanion }

case class Blogger(@Key("_id") _id: ObjectId, eMail: String, password: String) {

}

object Blogger extends ModelCompanion[Blogger, ObjectId] {
  val collection = common.Mongo.mongoDb("Blogger")
  val dao = new SalatDAO[Article, ObjectId](collection = collection) {}

}