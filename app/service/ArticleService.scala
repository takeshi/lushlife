package service
import com.novus.salat.global.ctx
import com.novus.salat.grater
import common.Service
import common.Mongo
import model.Article
import play.api.Logger
import javax.inject.Singleton

@Service
class ArticleService {

  implicit val g = grater[Article]

  def persist(a: Article) = {
    Logger.info("persist")
    Mongo.persist[Article](a)
  }

}