package common.markdown

import com.tristanhunt.knockoff._
import scala.util.{ Random }
import scala.xml.{ Group, Node, Text => XMLText, Unparsed }
import com.tristanhunt.knockoff.DefaultDiscounter._
import com.tristanhunt.knockoff._

object TextWriter {
  def write(markdown: String, lenght: Int): String = {
    val blocks = knockoff(markdown.trim()).filter { block =>
      block.isInstanceOf[CodeBlock] == false
    }
    val text = toText(blocks).toString()
    if (text.length() > lenght) {
      return text.substring(0, lenght) + "...."
    }
    return text
  }
}