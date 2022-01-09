package org.netherald.minejs.bukkit.utils

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.PacketTypeEnum
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player

object ProtocolUtil {

    lateinit var protocolManager: ProtocolManager

    fun init() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    fun createPacketContainer(isClient: Boolean, packetId: String, senderRaw: String): PacketContainer {
        lateinit var type: Class<out PacketTypeEnum>

        if(isClient) {
            when(senderRaw.lowercase()) {
                "play" -> type = PacketType.Play.Client::class.java
                "handshake" -> type = PacketType.Handshake.Client::class.java
                "login" -> type = PacketType.Login.Client::class.java
                "status" -> type = PacketType.Status.Client::class.java
                else -> type = PacketType.Play.Client::class.java
            }
        } else {
            when(senderRaw.lowercase()) {
                "play" -> type = PacketType.Play.Server::class.java
                "handshake" -> type = PacketType.Handshake.Server::class.java
                "login" -> type = PacketType.Login.Server::class.java
                "status" -> type = PacketType.Status.Server::class.java
                else -> type = PacketType.Play.Server::class.java
            }
        }

        val packetType = type.getField(packetId.uppercase()).get(null) as PacketType

        return PacketContainer(packetType)
    }

    fun sendPacket(player: Player, packetContainer: PacketContainer) {
        protocolManager.sendServerPacket(player, packetContainer)
    }

}