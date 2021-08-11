package org.netherald.javaskript.cli

class Player(val name: String) {
    fun sendMessage(string: String) {
        println("[$name] [PlayerReceive] $string")
    }
}