package com.example.chatvault

import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.util.HtmlUtils
import java.util.*

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

@RestController
class CsrfController {
    @GetMapping("/csrf")
    fun csrf(token: CsrfToken): CsrfToken {
        return token
    }
}

@Component
class WebSocketAuthenticatorService(
    private val userDetailsService: UserDetailsService
) {
    // This method MUST return a UsernamePasswordAuthenticationToken instance, the spring security chain is testing it with 'instanceof' later on. So don't use a subclass of it or any other class
    @Throws(AuthenticationException::class)
    fun getAuthenticatedOrFail(username: String?, password: String?): UsernamePasswordAuthenticationToken {
        if (username == null || username.trim { it <= ' ' }.isEmpty()) {
            throw AuthenticationCredentialsNotFoundException("Username was null or empty.")
        }
        if (password == null || password.trim { it <= ' ' }.isEmpty()) {
            throw AuthenticationCredentialsNotFoundException("Password was null or empty.")
        }

        if (!BCryptPasswordEncoder().matches(password, userDetailsService.loadUserByUsername(username).password)) {
            throw BadCredentialsException("Bad credentials for user $username")
        }

        // null credentials, do not pass the password along
        return UsernamePasswordAuthenticationToken(
            username,
            null,
            userDetailsService.loadUserByUsername(username).authorities
        )
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
            println("AuthFromSessionInterceptor: user is originally ${accessor.user}")
            accessor.user =
                getUsernamePasswordAuthenticationToken(accessor.sessionAttributes?.get("SPRING_SECURITY_CONTEXT"))
            println("Set user as ${accessor.user}")
        }
        return message
    }

    private fun getUsernamePasswordAuthenticationToken(securityContextImpl: Any?): UsernamePasswordAuthenticationToken? {
        return if (securityContextImpl is SecurityContextImpl && securityContextImpl.authentication is UsernamePasswordAuthenticationToken) {
            securityContextImpl.authentication as UsernamePasswordAuthenticationToken
        } else null
    }
}

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class WebSocketAuthenticationSecurityConfig(
    private val authFromSessionInterceptor: AuthFromSessionInterceptor
) :
    WebSocketMessageBrokerConfigurer {
    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(authFromSessionInterceptor)
    }
}