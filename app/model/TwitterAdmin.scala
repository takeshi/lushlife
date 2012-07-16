package model

import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import org.scala_tools.time.Imports._
import com.novus.salat.dao.{ SalatDAO, ModelCompanion }
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import com.novus.salat.annotations.raw.Key

case class TwitterAdmin(
  consumerKey: String,
  consumerSecret: String,
  @Key("_id") _id: ObjectId = null) {
}

object TwitterAdmin extends ModelCompanion[TwitterAdmin, ObjectId] {

  val collection = common.Mongo.mongoDb("TwitterAdmin")
  val dao = new SalatDAO[TwitterAdmin, ObjectId](collection = collection) {}

}