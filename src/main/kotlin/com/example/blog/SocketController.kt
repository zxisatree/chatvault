package com.example.blog

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class SocketController(private val userList: UserList) {
    @SubscribeMapping("/topic/{chatroom}")
    fun enterChatroom(): Message {
        println("There are ${userList.userCount()} users")
        return Message(HtmlUtils.htmlEscape("server"), "There are ${userList.userCount()} users in chatroom")
    }

    @MessageMapping("/send/{chatroom}")
    @SendTo("/topic/{chatroom}")
    fun receiveMessage(message: Message): Message {
        return Message(HtmlUtils.htmlEscape(message.username!!), HtmlUtils.htmlEscape(message.content!!))
    }
}