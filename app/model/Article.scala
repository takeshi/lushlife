package model
import com.novus.salat.Grater
import com.novus.salat.global.ctx
import com.novus.salat.grater
import play.api.libs.json.Reads
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import java.util.Date
import common.Validator
import com.novus.salat.annotations.raw.Key

import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import org.scala_tools.time.Imports._
import com.novus.salat.dao.{ SalatDAO, ModelCompanion }

case class Article(@Key("_id") _id: ObjectId, id: String, title: String, content: String, open: Boolean = true) {
}

object Article extends ModelCompanion[Article, ObjectId] {
  val collection = common.Mongo.mongoDb("Article")
  val dao = new SalatDAO[Article, ObjectId](collection = collection) {}

  def create(id: String): Article = Article(new ObjectId, id, "", "", false)

  implicit val g = grater[Article]

  implicit def validator = new Validator[Article] {
    def validate(article: Article): List[ErrorMessage] = {
      var list = List[ErrorMessage]()

      notEmpty(article.title, "title", "タイトル") { errorMessage =>
        list ::= errorMessage
      }

      notEmpty(article.content, "content", "コンテンツ") { errorMessage =>
        list ::= errorMessage
      }
      list
    }
  }
}