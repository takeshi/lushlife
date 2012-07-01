package common
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import play.api.Logger

object LoggingInterceptor extends MethodInterceptor {

  def invoke(context: MethodInvocation): Object = {
    Logger.info("IN  " + context.getMethod().toGenericString());
    val ret = context.proceed()
    Logger.info("OUT " + context.getMethod().toGenericString());
    ret
  }
}