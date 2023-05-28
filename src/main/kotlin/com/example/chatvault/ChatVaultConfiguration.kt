package com.example.chatvault

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
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
            //.disable()
            .ignoringRequestMatchers("/login/**")
            .ignoringRequestMatchers("/api/**")
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/api/**").hasRole("USER")
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/actuator/**").hasRole("ADMIN")
            .and()
            //.authorizeHttpRequests()
            //.requestMatchers("/**").hasRole("USER")
            //.and()
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
        //messages.simpDestMatchers("/**").hasRole("USER")
        messages.anyMessage().permitAll() //THIS WORKS
        return messages.build()
        //return AuthorityAuthorizationManager.hasRole("User")
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // Prefix for chatroom endpoints
        config.enableSimpleBroker("/topic")
    }

    // Initial STOMP connection endpoint
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chat")
    }

    @Bean
    @Description("Listens to dis/connect events")
    fun connectionListener(): ConnectionListener {
        return ConnectionListener()
    }

    @Bean
    @Description("Keeps track of connected users")
    fun userList(): UserList {
        return UserList()
    }
}