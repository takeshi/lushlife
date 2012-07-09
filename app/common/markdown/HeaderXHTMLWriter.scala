/*

## Output To XHTML

Knockoff's XHTMLWriter uses match expressions on it's object model to create a
very similar XHTML-style XML document.

Customization involves overriding one of these methods. At times, I've found it
easier to completely re-write or adjust the output method, so this technique may
not be 100% finished.

*/
package common.markdown

import com.tristanhunt.knockoff._
import scala.util.{ Random }
import scala.xml.{ Group, Node, Text => XMLText, Unparsed }
import com.tristanhunt.knockoff.DefaultDiscounter._
import com.tristanhunt.knockoff._

object HeaderXHTMLWriter {

  /** Creates a Group representation of the document. */
  def toXHTML(blocks: Seq[Block]): Node =
    Group(blocks.map(blockToXHTML(_)))

  def blockToXHTML: Block => Node = block => block match {
    case Header(level, spans, _) => headerToXHTML(level, spans)
  }

  def headerToXHTML: (Int, Seq[Span]) => Node = (level, spans) => {
    val spanned = spans.map(spanToXHTML(_))
    level match {
      case 1 => <li class="nav-header">{ spanned }</li>
      case 2 => <li><a href={"#"+spanned.head}>{ spanned }</a></li>
      case _ => <!--{sppaned} -->
    }
  }

  def markdown(markdown: String): String = {
    val blocks = knockoff(markdown.trim())

    val headers = blocks.filter(_.isInstanceOf[Header])

    (<ul class="nav nav-list">{ toXHTML(headers) }</ul>).toString
  }

}
