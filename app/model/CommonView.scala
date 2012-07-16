package model
import play.api.mvc.Request
import play.api.mvc._
import controllers.LoginController
import common.Lushlife
import common.Auth

case class CommonView(l: Boolean, req: Request[AnyContent], b: Blogger) {
  val logined = l
  var title = ""
  var scripts: List[String] = List()
  val request: Request[AnyContent] = req
  var blogger = b
}

object CommonView {
  def apply(req: Request[AnyContent]): CommonView = {
    Auth.blogger(req).map { blogger =>
      CommonView(true, req, blogger)
    }.getOrElse {
      CommonView(false, req, null)
    }
  }

}