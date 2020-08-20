/**
 * Edited 2020 UniGa
 */

package core.markdown

import core.node.StyleNode
import core.parser.ParseSpec
import core.parser.Parser
import core.parser.Rule
import eu.uniga.core.styles.TextStyle
import net.minecraft.text.Style
import net.minecraft.text.TextColor
import java.util.regex.Matcher
import java.util.regex.Pattern

object AdvancedMarkdownRules {

    private val PATTERN_BLOCK_QUOTE = Pattern.compile("^(?: *>>> ?(.+)| *>(?!>>) ?([^\\n]+\\n?))", Pattern.DOTALL)


    interface BlockQuoteState<Self: BlockQuoteState<Self>> {
        val isInQuote: Boolean
        fun newBlockQuoteState(isInQuote: Boolean): Self
    }

    /**
     * Examples:
     * > Quoted text
     *
     * >>> Quoted text
     * that is on
     * multiple lines
     */
    class BlockQuoteNode<F> : StyleNode<F, TextStyle>(Style.EMPTY.withColor(TextColor.fromRgb(0x888888)))

    // Use a block rule to ensure we only match at the beginning of a line.
    @JvmStatic
    fun <F, S: BlockQuoteState<S>> createBlockQuoteRule(): Rule.BlockRule<F, BlockQuoteNode<F>, S> =
        object : Rule.BlockRule<F, BlockQuoteNode<F>, S>(PATTERN_BLOCK_QUOTE) {
            override fun match(inspectionSource: CharSequence, lastCapture: String?, state: S): Matcher? {
                // Only do this if we aren't already in a quote.
                return if (state.isInQuote) { null } else { super.match(inspectionSource, lastCapture, state) }
            }

            override fun parse(matcher: Matcher, parser: Parser<F, in BlockQuoteNode<F>, S>, state: S): ParseSpec<F, BlockQuoteNode<F>, S> {
                val groupIndex = if (matcher.group(1) != null) { 1 } else { 2 }
                val newState = state.newBlockQuoteState(isInQuote = true)
                return ParseSpec.createNonterminal(BlockQuoteNode(), newState, matcher.start(groupIndex), matcher.end(groupIndex))
            }
        }
}