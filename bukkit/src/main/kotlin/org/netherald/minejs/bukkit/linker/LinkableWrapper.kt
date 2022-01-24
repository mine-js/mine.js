package org.netherald.minejs.bukkit.linker

interface LinkableWrapper<T> {
    fun unwrap(): T
}