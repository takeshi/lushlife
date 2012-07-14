package model
import play.api.mvc.Request
import play.api.mvc._
import controllers.LoginController
import common.Lushlife
import common.Auth

case class CommonView(l: Boolean, req: Request[AnyContent]) {
  val logined = l
  var title = "Title"
  var scripts: List[String] = List()
  val request: Request[AnyContent] = req
}

object CommonView {
  def apply(req: Request[AnyContent]): CommonView = {
    CommonView(Auth.logined(req), req)
  }

}