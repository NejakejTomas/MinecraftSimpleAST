/**
 * Edited 2020 UniGa
 */

package core.node

/**
 * Node representing simple text.
 */
open class TextNode (val content: String) : Node() {

  override fun toString() = "${javaClass.simpleName}[${getChildren()?.size}]: $content"
}
