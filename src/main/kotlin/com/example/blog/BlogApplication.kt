package com.example.blog

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BlogProperties::class)
class BlogApplication

var dotenv: Dotenv = dotenv {
    directory = "env.properties"
    ignoreIfMalformed = true
    ignoreIfMissing = true
}

fun main(args: Array<String>) {
    runApplication<BlogApplication>(*args)
}
