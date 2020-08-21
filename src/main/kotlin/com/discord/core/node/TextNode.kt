/**
 * Edited 2020 UniGa
 */

package com.discord.core.node

import net.minecraft.text.LiteralText

/**
 * Node representing simple text.
 * @param F The format context, can be any object that holds what's required for formatting. See [format].
 */
open class TextNode<F> (val content: String) : Node<F>() {

  override fun toString() = "${javaClass.simpleName}[${getChildren()?.size}]: $content"

  override fun format(renderContext: F): LiteralText {
    val text = LiteralText(content)

    getChildren()?.forEach {
      text.append(it.format(renderContext))
    }

    return text
  }
}
