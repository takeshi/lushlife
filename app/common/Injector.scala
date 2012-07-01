package common
import com.google.inject.Guice
import com.google.inject.AbstractModule
import com.google.inject.matcher.Matchers

object Injector {

  lazy val instance = {
    var m = new AbstractModule {
      override def configure() {
        bindInterceptor(Matchers.annotatedWith(classOf[Service]), Matchers.any(), LoggingInterceptor);
      }
    };
    Guice.createInjector(m);
  }

  def apply[T](implicit m: Manifest[T]): T = {
    instance.getInstance(m.erasure).asInstanceOf[T]
  }
}