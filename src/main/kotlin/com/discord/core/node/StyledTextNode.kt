package com.discord.core.node

import net.minecraft.text.LiteralText
import net.minecraft.text.Style

/**
 * @param T Type of Span to apply
 * @param F The format context, can be any object that holds what's required for formatting. See [format].
 */
open class StyledTextNode<F, T>(protected var style : Style?, protected var text : String?) : Node<F>() {

    override fun toString() = "${javaClass.simpleName} >\n" +
            getChildren()?.joinToString("\n->", prefix = ">>", postfix = "\n>|") {
                it.toString()
            }

    override fun format(renderContext: F): LiteralText {
        val text = LiteralText(text ?: "")
        if (style != null) text.style = style

        getChildren()?.forEach {
            text.append(it.format(renderContext))
        }

        return text
    }
}