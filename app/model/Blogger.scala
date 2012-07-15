package model

import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import org.scala_tools.time.Imports._
import com.novus.salat.dao.{ SalatDAO, ModelCompanion }

case class Blogger(@Key("_id") _id: ObjectId,
  email: String,
  password: String,
  admin: Boolean = false,
  twitterId: String = "") {

}

object Blogger extends ModelCompanion[Blogger, ObjectId] {

  def create(): Blogger = Blogger(new ObjectId, "", "")

  val collection = common.Mongo.mongoDb("Blogger")
  val dao = new SalatDAO[Blogger, ObjectId](collection = collection) {}

}