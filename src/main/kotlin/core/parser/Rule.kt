/**
 * Edited 2020 UniGa
 */

package core.parser

import core.node.Node
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @param F The format context, can be any object that holds what's required for formatting. See [Node.render]
 * @param T The type of nodes that are handled.
 * @param S The type of state that this rule needs to match and parse correctly. If the rule
 *          doesn't need state, this is typically an unbounded generic that's just passed through
 *          to the [ParseSpec]. If the rule *does* need state, this typically has a bound
 *          indicating at least one of the interfaces that the concrete state class implements.
 *          Note that states should **not** be mutated (since that would affect all subsequent
 *          nodes, not just child nodes), and therefore will likely need some sort of clone method
 *          in order to create a copy with different values to pass into the returned [ParseSpec].
 */
abstract class Rule<F, T : Node<F>, S>(val matcher: Matcher) {

  constructor(pattern: Pattern) : this(pattern.matcher(""))

  /**
   * Used to determine if the [Rule] applies to the [inspectionSource].
   *
   * @param inspectionSource Source string to apply the rule
   * @param lastCapture Last captured source occuring before [inspectionSource]
   *
   * @return a [Matcher] if the rule applies, else null
   */
  open fun match(inspectionSource: CharSequence, lastCapture: String?, state: S): Matcher? {
    matcher.reset(inspectionSource)
    return if (matcher.find()) matcher else null
  }

  abstract fun parse(matcher: Matcher, parser: Parser<F, in T, S>, state: S): ParseSpec<F, T, S>

  /**
   * A [Rule] that ensures that the [matcher] is only executed if the preceding capture was a newline.
   * e.g. this ensures that the regex parses from a newline.
   */
  abstract class BlockRule<F, T : Node<F>, S>(pattern: Pattern) : Rule<F, T, S>(pattern) {

    override fun match(inspectionSource: CharSequence, lastCapture: String?, state: S): Matcher? {
      if (lastCapture?.endsWith('\n') != false) {
        return super.match(inspectionSource, lastCapture, state)
      }
      return null
    }
  }
}

