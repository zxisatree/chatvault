package com.example.chatvault

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

class ConnectionListener {
    @Autowired
    private lateinit var userList: UserList

    @EventListener(SessionConnectEvent::class)
    fun handleSessionConnected(event: SessionConnectEvent) {
        val headers: SimpMessageHeaderAccessor = SimpMessageHeaderAccessor.wrap(event.message)
        val sessionId = headers.sessionId
        if (sessionId == null) {
            println("sessionId is null")
            return
        }
        userList.joinServer(sessionId)
    }

    @EventListener(SessionDisconnectEvent::class)
    fun handleSessionDisconnect(event: SessionDisconnectEvent) {
        val headers: SimpMessageHeaderAccessor = SimpMessageHeaderAccessor.wrap(event.message)
        val sessionId = headers.sessionId
        if (sessionId == null) {
            println("sessionId is null")
            return
        }
        userList.leaveServer(sessionId)
    }

    //    @EventListener
    //    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
    //        val headerAccessor = StompHeaderAccessor.wrap(event.message)
    //        val username = headerAccessor.sessionAttributes!!["username"] as String?
    //        if (username != null) {
    //            println("User Disconnected : $username")
    //            val chatMessage = Message("server", "User Disconnected : $username")
    //            messagingTemplate.convertAndSend("/topic/public", chatMessage)
    //        }
    //    }
}