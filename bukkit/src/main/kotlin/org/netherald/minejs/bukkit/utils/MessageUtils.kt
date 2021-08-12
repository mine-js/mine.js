package org.netherald.minejs.bukkit.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.markdown.DiscordFlavor
import net.kyori.adventure.text.minimessage.transformation.TransformationType

object MessageUtils {

    val miniMessage = MiniMessage.builder()
        .removeDefaultTransformations()
        .transformation(TransformationType.COLOR)
        .transformation(TransformationType.DECORATION)
        .markdown()
        .markdownFlavor(DiscordFlavor.get())
        .build()

    fun build(str: String) : Component = miniMessage.parse(str)

    fun toMiniMessage(comp: Component?) : String? {
        if(comp != null)
            return miniMessage.serialize(comp)
        else
            return null
    }

}