package com.example.blog

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class BlogConfiguration {
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
            .authorizeHttpRequests()
            .requestMatchers("/api/**").hasRole("USER")
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
        val users = JdbcUserDetailsManager(dataSource)
        return users
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    //    Seeds the database on every startup
    //@Bean
    //fun databaseInitializer(
    //    userRepository: UserRepository,
    //    articleRepository: ArticleRepository
    //) = ApplicationRunner {
    //
    //    val johnDoe = userRepository.save(DbUser("johnDoe", "password"))
    //    articleRepository.save(
    //        Article(
    //            title = "Lorem",
    //            headline = "Lorem",
    //            content = "dolor sit amet",
    //            author = johnDoe
    //        )
    //    )
    //    articleRepository.save(
    //        Article(
    //            title = "Ipsum",
    //            headline = "Ipsum",
    //            content = "dolor sit amet",
    //            author = johnDoe
    //        )
    //    )
    //}
}