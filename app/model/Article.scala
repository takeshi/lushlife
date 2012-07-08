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

case class Article(id: String, title: String, content: String, date: Date) {
  def this(id: String) = this(id, "", "", null)
  def this(id: String, title: String, content: String) = this(id, title, content, null)
}

object Article {
  implicit def articleReads = new Reads[Article] {
    def reads(json: JsValue): Article = new Article(
      (json \ "id").as[String],
      (json \ "title").as[String],
      (json \ "content").as[String])
  }

  implicit def articleWrites = new Writes[Article] {
    def writes(ts: Article) = JsObject(Seq(
      "id" -> JsString(ts.id),
      "title" -> JsString(ts.title),
      "content" -> JsString(ts.content)))
  }

  implicit val g = grater[Article]

  implicit def validator = new Validator[Article] {
    def validate(article: Article): List[ErrorMessage] = {
      var list = List[ErrorMessage]()

      notEmpty(article.id, "id", "ID") { errorMessage =>
        list ::= errorMessage
      }
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