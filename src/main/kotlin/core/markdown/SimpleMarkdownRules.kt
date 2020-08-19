/**
 * Edited 2020 UniGa
 */

package core.markdown

import core.node.Node
import core.node.StyleNode
import core.node.TextNode
import core.parser.ParseSpec
import core.parser.Parser
import core.parser.Rule
import eu.uniga.core.styles.TextStyle
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object SimpleMarkdownRules {

  private val PATTERN_BOLD = Pattern.compile("^\\*\\*([\\s\\S]+?)\\*\\*(?!\\*)")
  private val PATTERN_UNDERLINE = Pattern.compile("^__([\\s\\S]+?)__(?!_)")
  private val PATTERN_STRIKETHRU = Pattern.compile("^~~(?=\\S)([\\s\\S]*?\\S)~~")
  private val PATTERN_NEWLINE = Pattern.compile("""^(?:\n *)*\n""")
  private val PATTERN_TEXT = Pattern.compile("^[\\s\\S]+?(?=[^0-9A-Za-z\\s\\u00c0-\\uffff]|\\n| {2,}\\n|\\w+:\\S|$)")
  private val PATTERN_ESCAPE = Pattern.compile("^\\\\([^0-9A-Za-z\\s])")

  private val PATTERN_ITALICS = Pattern.compile(
      // only match _s surrounding words.
      "^\\b_" + "((?:__|\\\\[\\s\\S]|[^\\\\_])+?)_" + "\\b" +
          "|" +
          // Or match *s that are followed by a non-space:
          "^\\*(?=\\S)(" +
          // Match any of:
          //  - `**`: so that bolds inside italics don't close the
          // italics
          //  - whitespace
          //  - non-whitespace, non-* characters
          "(?:\\*\\*|\\s+(?:[^*\\s]|\\*\\*)|[^\\s*])+?" +
          // followed by a non-space, non-* then *
          ")\\*(?!\\*)"
  )

  fun <S> createBoldRule(): Rule<Node, S> =
      createSimpleStyleRule(PATTERN_BOLD) { listOf(TextStyle.Bold) }

  fun <S> createUnderlineRule(): Rule<Node, S> =
      createSimpleStyleRule(PATTERN_UNDERLINE) { listOf(TextStyle.Underlined) }

  fun <S> createStrikethruRule(): Rule<Node, S> =
      createSimpleStyleRule(PATTERN_STRIKETHRU) { listOf(TextStyle.StrikeThrough) }

  fun <S> createTextRule(): Rule<Node, S> {
    return object : Rule<Node, S>(PATTERN_TEXT) {
      override fun parse(matcher: Matcher, parser: Parser<in Node, S>, state: S): ParseSpec<Node, S> {
        val node = TextNode(matcher.group())
        return ParseSpec.createTerminal(node, state)
      }
    }
  }
  fun <S> createNewlineRule(): Rule<Node, S> {
    return object : Rule.BlockRule<Node, S>(PATTERN_NEWLINE) {
      override fun parse(matcher: Matcher, parser: Parser<in Node, S>, state: S): ParseSpec<Node, S> {
        val node = TextNode("\n")
        return ParseSpec.createTerminal(node, state)
      }
    }
  }

  fun <S> createEscapeRule(): Rule<Node, S> {
    return object : Rule<Node, S>(PATTERN_ESCAPE) {
      override fun parse(matcher: Matcher, parser: Parser<in Node, S>, state: S): ParseSpec<Node, S> {
        return ParseSpec.createTerminal(TextNode(matcher.group(1)), state)
      }
    }
  }

  fun <S> createItalicsRule(): Rule<Node, S> {
    return object : Rule<Node, S>(PATTERN_ITALICS) {
      override fun parse(matcher: Matcher, parser: Parser<in Node, S>, state: S): ParseSpec<Node, S> {
        val startIndex: Int
        val endIndex: Int
        val asteriskMatch = matcher.group(2)
        if (asteriskMatch != null && asteriskMatch.length > 0) {
          startIndex = matcher.start(2)
          endIndex = matcher.end(2)
        } else {
          startIndex = matcher.start(1)
          endIndex = matcher.end(1)
        }

        val styles = ArrayList<TextStyle>(1)
        styles.add(TextStyle.Italic)

        val node = StyleNode<TextStyle>(styles)
        return ParseSpec.createNonterminal(node, state, startIndex, endIndex)
      }
    }
  }

  @JvmOverloads @JvmStatic
  fun <S> createSimpleMarkdownRules(includeTextRule: Boolean = true): MutableList<Rule<Node, S>> {
    val rules = ArrayList<Rule<Node, S>>()
    rules.add(createEscapeRule())
    rules.add(createNewlineRule())
    rules.add(createBoldRule())
    rules.add(createUnderlineRule())
    rules.add(createItalicsRule())
    rules.add(createStrikethruRule())
    if (includeTextRule) {
      rules.add(createTextRule())
    }
    return rules
  }

  @JvmStatic
  fun <S> createSimpleStyleRule(pattern: Pattern, styleFactory: () -> List<TextStyle>) =
      object : Rule<Node, S>(pattern) {
        override fun parse(matcher: Matcher, parser: Parser<in Node, S>, state: S): ParseSpec<Node, S> {
          val node = StyleNode<TextStyle>(styleFactory())
          return ParseSpec.createNonterminal(node, state, matcher.start(1), matcher.end(1))
        }
      }
}

