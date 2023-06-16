package com.zxisatree.chatvault

import com.zxisatree.chatvault.chat.ConnectionListener
import com.zxisatree.chatvault.chat.UserList
import com.zxisatree.chatvault.chat.socketTopicPrefix
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
@EnableWebSocketSecurity
@EnableWebSocketMessageBroker
class ChatVaultConfiguration : WebSocketMessageBrokerConfigurer {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins("*")
            }
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf()
            .ignoringRequestMatchers("/api/**")
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/actuator/**").hasRole("ADMIN")
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/api/**").hasRole("USER")
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/chat.html").hasRole("USER")
            .and()
            .authorizeHttpRequests()
            .anyRequest().permitAll()
            .and()
            .formLogin()
            .permitAll()
            .and()
            .logout()
            .permitAll()
        return http.build()
    }

    @Bean
    fun userDetailsService(dataSource: DataSource): UserDetailsService {
        return JdbcUserDetailsManager(dataSource)
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun messageAuthorizationManager(messages: MessageMatcherDelegatingAuthorizationManager.Builder): AuthorizationManager<Message<*>> {
        messages.simpDestMatchers("/**").hasRole("USER").nullDestMatcher().hasRole("USER")
            .anyMessage().denyAll()
        return messages.build()
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // Prefix for chatroom endpoints
        config.enableSimpleBroker(socketTopicPrefix)
    }

    // Initial STOMP connection endpoint
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        val handshakeInterceptor = HttpSessionHandshakeInterceptor(listOf("SPRING_SECURITY_CONTEXT"))
        registry.addEndpoint("/chat").addInterceptors(handshakeInterceptor)
    }

    @Bean
    fun connectionListener(): ConnectionListener {
        return ConnectionListener()
    }

    @Bean
    fun userList(): UserList {
        return UserList()
    }
}