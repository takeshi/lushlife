package model
import play.api.mvc.Request
import play.api.mvc._
import controllers.LoginController

case class CommonView(l: Boolean, req: Request[AnyContent]) {
  val logined = l
  var title = "Title"
  var scripts: List[String] = List()
  val request: Request[AnyContent] = req
}

object CommonView {
  def apply(req: Request[AnyContent]): CommonView = {
    CommonView(LoginController.logined(req), req)
  }

}