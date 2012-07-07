package common
import java.util.concurrent.atomic.AtomicInteger

object PageCounter {
  val counter = new AtomicInteger

  def next(): Int = {
    counter.getAndIncrement()
  }

}