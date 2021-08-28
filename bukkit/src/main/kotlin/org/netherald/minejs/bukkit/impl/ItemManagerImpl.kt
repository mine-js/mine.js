package org.netherald.minejs.bukkit.impl

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.netherald.minejs.common.ItemManager
import org.netherald.minejs.bukkit.utils.ObjectUtils.createItemStackObject

class ItemManagerImpl : ItemManager {

    override fun itemOf(runtime: V8, name: String): V8Object {
        val material = Material.getMaterial(name) ?: return V8Object(runtime)
        return V8Object(runtime).apply(createItemStackObject(ItemStack(material),runtime))
    }
}