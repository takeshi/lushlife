package controllers
import play.api.mvc.Controller
import model.CommonView
import common.Auth
import model.Blogger
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import common.Logger
import org.bson.types.ObjectId
import common.Mongo
import play.api.libs.json.Json

class SettingsController {
}

object SettingsController extends Controller {
  def logger = Logger[SettingsController]

  def index = LushlifeAction { req =>
    def c = CommonView(req)
    def bloggerOid = Auth.logined(req)

    Auth.blogger(req).map { blogger =>
      Ok(views.html.settings(blogger, c))
    }.getOrElse {
      logger.info("Blogger $oid not found")
      Redirect("/login?url=" + req.uri)
    }
  }

  def admin = LushlifeAction { req =>
    def c = CommonView(req)
    Auth.blogger(req).map { blogger =>
      val bloggers = Blogger.findAll().toList
      if (bloggers.length == 1) {
        Ok(views.html.admin(bloggers, c))
      } else {
        if (blogger.admin) {
          Ok(views.html.admin(bloggers, c))
        } else {
          Redirect("/login?url=" + req.uri)
        }
      }
    }.getOrElse {
      Redirect("/login?url=" + req.uri)
    }
  }

  object Rerender {
    def update = LushlifeAction.RequiredAuth { req =>
      def c = CommonView(req)
      Auth.blogger(req).map { blogger =>
        req.body.asFormUrlEncoded.map { form =>
          val oid = form.get("oid").get.head
          val admin = form.get("admin").get.head

          if (Blogger.collection.count == 1 || blogger.admin) {

            Mongo.mongoDb("Blogger").update(
              MongoDBObject("_id" -> new ObjectId(oid)),
              MongoDBObject("$set" -> MongoDBObject("admin" -> !admin.toBoolean)))

            Blogger.findOneById(new ObjectId(oid)).map { blogger =>
              Ok(Json.toJson(
                Map("oid" -> blogger._id.toStringMongod(),
                  "admin" -> blogger.admin.toString)).toString)
            }
          }.getOrElse {
            BadRequest(" " + req)
          }
          else {
            Unauthorized("Unauthenticated User " + blogger.email)
          }
        }.getOrElse {
          BadRequest(" " + req)
        }
      }.getOrElse {
        BadRequest(" " + req)
      }
    }
  }
}