package common.markdown
import org.junit.Test
import scala.util.parsing.combinator.RegexParsers
import util.parsing.combinator._
import com.tristanhunt.knockoff.DefaultDiscounter._
import com.tristanhunt.knockoff._

class MarkDonwParserTest {
  @Test
  def test() {
    val markdown = """# I'm the *title*
    scala
    
      
      
- And I'm a paragraph
- 3
- 3
- 3
      
1. 3
2. 3
3. 3
      
      
      """ 

    println(XHTMLWriter.markdown(markdown))
  }

}