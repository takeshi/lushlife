package common
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

class LoggingInterceptor
object LoggingInterceptor extends MethodInterceptor {

  def logger = Logger[LoggingInterceptor]

  def invoke(context: MethodInvocation): Object = {
    try {
      logger.info("IN  {}", context.getMethod().toGenericString());
      val ret = context.proceed()
      logger.info("OUT {}", context.getMethod().toGenericString());
      ret
    } catch {
      case e: Throwable =>
        logger.info("OUTE {}", context.getMethod().toGenericString(), e);
        throw e
    }
  }
}