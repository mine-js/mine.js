package org.netherald.minejs.bukkit.impl

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.netherald.minejs.common.interfaces.ItemManager
import org.netherald.minejs.bukkit.utils.ObjectUtils.createItemStackObject

class ItemManagerImpl : ItemManager {
    override fun itemOf(runtime: V8, material: String): V8Object {
        val bukkitMaterial = Material.getMaterial(material.uppercase()) ?: return V8Object(runtime)
        return V8Object(runtime).apply(createItemStackObject(ItemStack(bukkitMaterial),runtime))
    }
}