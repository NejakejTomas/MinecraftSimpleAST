/**
 * Edited 2020 UniGa
 */

package core.node

/**
 * @param T Type of Span to apply
 */
open class StyleNode<T>(val styles: List<T>) : Node() {

  override fun toString() = "${javaClass.simpleName} >\n" +
      getChildren()?.joinToString("\n->", prefix = ">>", postfix = "\n>|") {
        it.toString()
      }
}
