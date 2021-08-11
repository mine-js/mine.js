package org.netherald.minejs.cli

class Player(val name: String) {
    fun sendMessage(string: String) {
        println("[$name] [PlayerReceive] $string")
    }
}