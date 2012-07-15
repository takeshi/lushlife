package controllers
import play.api.mvc.Controller
import twitter4j.TwitterFactory
import model.TwitterAdmin
import com.mongodb.casbah.commons.MongoDBObject
import common.Auth
import model.TwitterUser
import common.Logger
import model.CommonView
import common.RedisClientManager
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken
import common.TwitterSupport
import model.Blogger
import org.bson.types.ObjectId

class TwitterController {}
object TwitterController extends Controller {
  def logger = Logger[TwitterController]

  def index = LushlifeAction { req =>
    TwitterAdmin.findOne(MongoDBObject()).map { admin =>
      val twitter = new TwitterFactory().getInstance();
      twitter.setOAuthConsumer(admin.consumerKey,
        admin.consumerSecret);
      val requestToken = twitter.getOAuthRequestToken();
      logger.info("request token {}", requestToken)
      TwitterSupport.requestTokenSecret(req, requestToken)
      Redirect(requestToken.getAuthorizationURL())
    }.getOrElse {
      Forbidden("twitter settings disabled")
    }
  }

  def callback = LushlifeAction { req =>
    TwitterAdmin.findOne(MongoDBObject()).map { admin =>
      val token = req.queryString.get("oauth_token").get.head
      val verifier = req.queryString.get("oauth_verifier").get.head

      val twitter = new TwitterFactory().getInstance();
      logger.info("TwitterAdmin {}", admin)
      twitter.setOAuthConsumer(admin.consumerKey,
        admin.consumerSecret);
      logger.info("token {} secret {} ", token, verifier)

      TwitterSupport.requestTokenSecret(req, token).map { secret =>
        val requestToken = new RequestToken(token, secret)
        logger.info("request token {} verifier {}", requestToken, verifier)
        val accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
        val user = new TwitterUser(accessToken)

        val cookie = Blogger.findOne(MongoDBObject("twitterName" -> user.name, "accountType" -> "twitter")).map { blogger =>
          Auth.login(req, blogger)
        }.getOrElse {
          val blogger = Blogger(new ObjectId, "", "", false, user.name, "twitter")
          Blogger.save(blogger)
          Auth.login(req, blogger)
        }
        TwitterSupport.write(req, user)
        Redirect("/").withCookies(cookie)
      }.getOrElse {
        Forbidden("twitter settings disabled")
      }
    }.getOrElse {
      Forbidden("twitter settings disabled")
    }
  }
}