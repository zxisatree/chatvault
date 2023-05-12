package com.example.blog

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class BlogConfiguration {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins("*")
            }
        }
    }

    //    For H2 databases
    //    @Bean
    //    fun databaseInitializer(userRepository: UserRepository,
    //                            articleRepository: ArticleRepository) = ApplicationRunner {
    //
    //        val johnDoe = userRepository.save(User("johnDoe", "John", "Doe"))
    //        articleRepository.save(Article(
    //            title = "Lorem",
    //            headline = "Lorem",
    //            content = "dolor sit amet",
    //            author = johnDoe
    //        ))
    //        articleRepository.save(Article(
    //            title = "Ipsum",
    //            headline = "Ipsum",
    //            content = "dolor sit amet",
    //            author = johnDoe
    //        ))
    //    }
}