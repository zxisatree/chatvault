package com.example.blog

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class SocketController(private val userList: UserList) {
    @SubscribeMapping("/topic/{chatroom}")
    fun enterChatroom(): ServerMessage {
        val userCount = userList.userCount()
        return ServerMessage("There ${if (userCount >= 2) "are $userCount users" else "is $userCount user"} in the chatroom")
    }

    @SubscribeMapping("/info/{chatroom}")
    fun getChatroomInfo(): ServerMessage {
        return ServerMessage("Users connected: $userList")
    }

    @MessageMapping("/send/{chatroom}")
    @SendTo("/topic/{chatroom}")
    fun receiveMessage(message: Message): Message {
        return Message(HtmlUtils.htmlEscape(message.content), HtmlUtils.htmlEscape(message.username))
    }
}