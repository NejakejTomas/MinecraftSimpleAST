/**
 * Edited 2020 UniGa
 */

package core.parser

import core.node.Node

/**
 * Facilitates fast parsing of the source text.
 *
 *
 * For nonterminal subtrees, the provided root will be added to the main, and text between
 * startIndex (inclusive) and endIndex (exclusive) will continue to be parsed into Nodes and
 * added as children under this root.
 *
 *
 * For terminal subtrees, the root will simply be added to the tree and no additional parsing will
 * take place on the text.
 *
 * @param T The type of node that this contains.
 * @param S The type of state that child nodes will use. This is mainly used to just pass through
 *          the state back to the parser.
 */
class ParseSpec<T : Node, S> {
  val root: T?
  val isTerminal: Boolean
  val state: S
  var startIndex: Int = 0
  var endIndex: Int = 0

  constructor(root: T?, state: S, startIndex: Int, endIndex: Int) {
    this.root = root
    this.state = state
    this.isTerminal = false
    this.startIndex = startIndex
    this.endIndex = endIndex
  }

  constructor(root: T?, state: S) {
    this.root = root
    this.state = state
    this.isTerminal = true
  }

  fun applyOffset(offset: Int) {
    startIndex += offset
    endIndex += offset
  }

  companion object {

    @JvmStatic
    fun <T : Node, S> createNonterminal(node: T?, state: S, startIndex: Int, endIndex: Int): ParseSpec<T, S> {
      return ParseSpec(node, state, startIndex, endIndex)
    }

    @JvmStatic
    fun <T : Node, S> createTerminal(node: T?, state: S): ParseSpec<T, S> {
      return ParseSpec(node, state)
    }
  }
}

