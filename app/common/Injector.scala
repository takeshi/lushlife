package common
import java.lang.reflect.Method
import java.lang.Object

import com.google.inject.matcher.AbstractMatcher
import com.google.inject.matcher.Matchers
import com.google.inject.AbstractModule
import com.google.inject.Guice

object Injector {

  lazy val instance = {
    var m = new AbstractModule {
      override def configure() {
        bindInterceptor(Matchers.inSubpackage("service"), new AbstractMatcher[Method] {
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