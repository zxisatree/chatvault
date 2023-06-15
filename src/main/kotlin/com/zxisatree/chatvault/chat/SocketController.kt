package com.zxisatree.chatvault.chat

import com.zxisatree.chatvault.ChatroomDoesNotExistException
import com.zxisatree.chatvault.InvalidMessageException
import com.zxisatree.chatvault.Message
import com.zxisatree.chatvault.ServerMessage
import com.zxisatree.chatvault.RoomRepository
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.util.HtmlUtils
import java.security.Principal
import java.util.*

const val socketTopicPrefix = "/topic"
const val socketInfoPrefix = "/info"
const val socketSendPrefix = "/send"

@Controller
class SocketController(private val userList: UserList, private val roomRepository: RoomRepository, private val simpMessagingTemplate: SimpMessagingTemplate) {
    @SubscribeMapping("$socketTopicPrefix/{chatroom}")
    fun enterChatroom(@DestinationVariable chatroom: String, principal: Principal): ServerMessage {
        if (roomRepository.findByName(chatroom) == null) {
            throw ChatroomDoesNotExistException(chatroom)
        }
        val userCount = userList.userCount()
        simpMessagingTemplate.convertAndSend("/topic/$chatroom", ServerMessage("${principal.name} joined the server"))
        return ServerMessage("There ${if (userCount >= 2) "are $userCount users" else "is $userCount user"} in the chatroom")
    }

    @SubscribeMapping("$socketInfoPrefix/{chatroom}")
    fun getChatroomInfo(): ServerMessage {
        return ServerMessage("Users connected: $userList")
    }

    @MessageMapping("$socketSendPrefix/{chatroom}")
    @SendTo("/topic/{chatroom}")
    fun receiveMessage(message: Message, principal: Principal): Message {
        return Message(HtmlUtils.htmlEscape(message.content), HtmlUtils.htmlEscape(principal.name))
    }
}

// CSRF for websockets cannot be disabled without explicitly creating the web socket security class ourselves
@RestController
class CsrfController {
    @GetMapping("/csrf")
    fun csrf(token: CsrfToken): CsrfToken {
        return token
    }
}

@Component
class AuthFromSessionInterceptor : ChannelInterceptor {
    override fun preSend(
        message: org.springframework.messaging.Message<*>,
        channel: MessageChannel
    ): org.springframework.messaging.Message<*>? {
        val accessor: StompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: throw BadCredentialsException("MessageHeaderAccessor.getAccessor failed")
        if (StompCommand.CONNECT == accessor.command) {
            accessor.user =
                getUsernamePasswordAuthenticationToken(accessor.sessionAttributes?.get("SPRING_SECURITY_CONTEXT"))
        }
        return message
    }

    private fun getUsernamePasswordAuthenticationToken(securityContextImpl: Any?): UsernamePasswordAuthenticationToken? {
        return if (securityContextImpl is SecurityContextImpl && securityContextImpl.authentication is UsernamePasswordAuthenticationToken) {
            securityContextImpl.authentication as UsernamePasswordAuthenticationToken
        } else null
    }
}

@Component
class MessageValidityInterceptor(private val roomRepository: RoomRepository) : ChannelInterceptor {
    override fun preSend(
        message: org.springframework.messaging.Message<*>,
        channel: MessageChannel
    ): org.springframework.messaging.Message<*>? {
        // Check if the server is valid
        val accessor: StompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: throw InvalidMessageException("MessageHeaderAccessor.getAccessor failed")
        val destination = accessor.destination ?: return message
        var destinationWithoutPrefix: String
        if (destination.startsWith("$socketTopicPrefix/")) {
            destinationWithoutPrefix = destination.substringAfter("$socketTopicPrefix/")
        } else if (destination.startsWith("$socketInfoPrefix/")) {
            destinationWithoutPrefix = destination.substringAfter("$socketInfoPrefix/")
        } else if (destination.startsWith("$socketSendPrefix/")) {
            destinationWithoutPrefix = destination.substringAfter("$socketSendPrefix/")
        } else {
            throw InvalidMessageException("destination has invalid prefix, does not map to any known endpoint")
        }
        roomRepository.findByName(destinationWithoutPrefix) ?: throw InvalidMessageException("room does not exist")
        return message
    }
}

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class WebSocketAuthenticationSecurityConfig(
    private val authFromSessionInterceptor: AuthFromSessionInterceptor,
    private val messageValidityInterceptor: MessageValidityInterceptor
) :
    WebSocketMessageBrokerConfigurer {
    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(messageValidityInterceptor).interceptors(authFromSessionInterceptor)
    }
}