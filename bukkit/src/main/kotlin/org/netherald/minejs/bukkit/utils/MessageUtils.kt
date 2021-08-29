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
        .transformation(TransformationType.HOVER_EVENT)
        .transformation(TransformationType.GRADIENT)
        .transformation(TransformationType.CLICK_EVENT)
        .transformation(TransformationType.RAINBOW)
        .transformation(TransformationType.RESET)
        .transformation(TransformationType.FONT)
        .transformation(TransformationType.KEYBIND)
        .transformation(TransformationType.TRANSLATABLE)
        .markdown()
        .markdownFlavor(DiscordFlavor.get())
        .build()

    fun build(str: String) : Component = miniMessage.parse(str)

    fun toMiniMessage(comp: Component) : String = miniMessage.serialize(comp)

}