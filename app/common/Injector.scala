package common
import com.google.inject.Guice
import com.google.inject.AbstractModule
import com.google.inject.matcher.Matchers
import com.google.inject.matcher.Matcher
import com.google.inject.matcher.AbstractMatcher
import java.lang.reflect.Method

object Injector {

  lazy val instance = {
    var m = new AbstractModule {
      override def configure() {
        bindInterceptor(Matchers.annotatedWith(classOf[Service]), new AbstractMatcher[Method] {
          override def matches(m: Method): Boolean = {
            m.getDeclaringClass() != classOf[Object]
          }

        }, LoggingInterceptor);
      }
    };
    Guice.createInjector(m);
  }

  def apply[T](implicit m: Manifest[T]): T = {
    instance.getInstance(m.erasure).asInstanceOf[T]
  }
}