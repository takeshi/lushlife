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

object XHTMLWriter {

  /** Creates a Group representation of the document. */
  def toXHTML(blocks: Seq[Block]): Node =
    Group(blocks.map(blockToXHTML(_)))

  def blockToXHTML: Block => Node = block => block match {
    case Paragraph(spans, _) => paragraphToXHTML(spans)
    case Header(level, spans, _) => headerToXHTML(level, spans)
    case LinkDefinition(_, _, _, _) => Group(Nil)
    case Blockquote(children, _) => blockquoteToXHTML(children)
    case CodeBlock(text, _) => codeToXHTML(text)
    case HorizontalRule(_) => hrXHTML
    case OrderedItem(children, _) => liToXHTML(children)
    case UnorderedItem(children, _) => liToXHTML(children)
    case OrderedList(items) => olToXHTML(items)
    case UnorderedList(items) => ulToXHTML(items)
  }

  def paragraphToXHTML: Seq[Span] => Node = spans => {
    def isHTML(s: Span) = s match {
      case y: HTMLSpan => true
      case com.tristanhunt.knockoff.Text(content) => if (content.trim.isEmpty) true else false
      case _ => false
    }
    if (spans.forall(isHTML))
      Group(spans.map(spanToXHTML(_)))
    else
      <p>{ spans.map(spanToXHTML(_)) }</p>
  }

  def headerToXHTML: (Int, Seq[Span]) => Node = (level, spans) => {
    val spanned = spans.map(spanToXHTML(_))
    level match {
      case 1 => <h3>{ spanned }</h3>
      case 2 => <h3 id={ spanned }>{ spanned }</h3>
      case 3 => <h3>{ spanned }</h3>
      case 4 => <h4>{ spanned }</h4>
      case 5 => <h5>{ spanned }</h5>
      case 6 => <h6>{ spanned }</h6>
      case _ => <div class={ "header" + level }>{ spanned }</div>
    }
  }

  def blockquoteToXHTML: Seq[Block] => Node =
    children => <blockquote>{ children.map(blockToXHTML(_)) }</blockquote>

  def codeToXHTML: com.tristanhunt.knockoff.Text => Node =
    text => {
      val code = text.content.trim()
      val fistLine = code.indexOf("\n") + 1;

      if (code.startsWith("#scala")) {
        <pre class="prettyprint lang-scala">{ code.substring(fistLine) }</pre>
      } else if (code.startsWith("#java")) {
        <pre class="prettyprint lang-java">{ code.substring(fistLine) }</pre>
      } else if (code.startsWith("#coffee")) {
        <pre class="prettyprint lang-coffee">{ code.substring(fistLine) }</pre>
      } else if (code.startsWith("#less")) {
        <pre class="prettyprint lang-css">{ code.substring(fistLine) }</pre>
      } else if (code.startsWith("#css")) {
        <pre class="prettyprint lang-css">{ code.substring(fistLine) }</pre>
      } else {
        <pre class="prettyprint"><code>{ code }</code></pre>
      }
    }

  def hrXHTML: Node = <hr/>

  def liToXHTML: Seq[Block] => Node =
    children => <li>{ simpleOrComplex(children) }</li>

  private def simpleOrComplex(children: Seq[Block]): Seq[Node] = {
    if (children.length == 1)
      children.head match {
        case Paragraph(spans, _) => spans.map(spanToXHTML(_))
        case _ => children.map(blockToXHTML(_))
      }
    else
      children.map(blockToXHTML(_))
  }

  def olToXHTML: Seq[Block] => Node =
    items => <ol>{ items.map(blockToXHTML(_)) }</ol>

  def ulToXHTML: Seq[Block] => Node =
    items => <ul>{ items.map(blockToXHTML(_)) }</ul>

  def spanToXHTML: Span => Node = span => span match {
    case com.tristanhunt.knockoff.Text(content) => textToXHTML(content)
    case HTMLSpan(html) => htmlSpanToXHTML(html)
    case CodeSpan(code) => codeSpanToXHTML(code)
    case com.tristanhunt.knockoff.Strong(children) => strongToXHTML(children)
    case Emphasis(children) => emphasisToXHTML(children)
    case com.tristanhunt.knockoff.Link(children, url, title) => linkToXHTML(children, url, title)
    case IndirectLink(children, definition) =>
      linkToXHTML(children, definition.url, definition.title)
    case ImageLink(children, url, title) => imageLinkToXHTML(children, url, title)
    case IndirectImageLink(children, definition) =>
      imageLinkToXHTML(children, definition.url, definition.title)
  }

  def textToXHTML: String => Node = content => XMLText(unescape(content))

  def htmlSpanToXHTML: String => Node = html => Unparsed(html)

  def codeSpanToXHTML: String => Node = code => <code>{ code }</code>

  def strongToXHTML: Seq[Span] => Node =
    spans => <strong>{ spans.map(spanToXHTML(_)) }</strong>

  def emphasisToXHTML: Seq[Span] => Node =
    spans => <em>{ spans.map(spanToXHTML(_)) }</em>

  def linkToXHTML: (Seq[Span], String, Option[String]) => Node = {
    (spans, url, title) =>
      <a href={ escapeURL(url) } title={ title.getOrElse(null) }>{
        spans.map(spanToXHTML(_))
      }</a>
  }

  def imageLinkToXHTML: (Seq[Span], String, Option[String]) => Node = {
    (spans, url, title) =>
      <img src={ url } title={ title.getOrElse(null) } alt={ spans.map(spanToXHTML(_)) }></img>
  }

  def escapeURL(url: String): Node = {
    if (url.startsWith("mailto:")) {
      val rand = new Random
      val mixed = url.map { ch =>
        rand.nextInt(2) match {
          case 0 => java.lang.String.format("&#%d;", int2Integer(ch.toInt))
          case 1 => java.lang.String.format("&#x%s;", ch.toInt.toHexString)
        }
      }.mkString("")
      Unparsed(mixed)
    } else {
      XMLText(url)
    }
  }

  // TODO put this somewhere else
  val escapeableChars = List("\\", "`", "*", "_", "{", "}", "[", "]", "(", ")",
    "#", "+", "-", ".", "!", ">")

  def unescape(source: String): String = {
    var buf: String = source
    for ((escaped, unescaped) <- escapeableChars.map(ch => ("\\" + ch, ch)))
      buf = buf.replace(escaped, unescaped)
    buf
  }

  def markdown(markdown: String): String = {
    val blocks = knockoff(markdown.trim())
    toXHTML(blocks).toString()
  }

}
