package model
import play.api.libs.json.Writes
import play.api.libs.json.JsObject
import play.api.libs.json.JsString

case class ErrorMessage(property: String, message: String) {

}

object ErrorMessage {

  implicit def articleWrites = new Writes[ErrorMessage] {
    def writes(ts: ErrorMessage) = JsObject(Seq(
      "property" -> JsString(ts.property),
      "message" -> JsString(ts.message)))
  }
}