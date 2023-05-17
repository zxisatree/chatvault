package com.example.blog

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.HtmlUtils
import java.security.Principal
import kotlin.jvm.optionals.getOrElse


@RestController
@RequestMapping("/api/article")
class ArticleController(private val articleRepository: ArticleRepository, private val userRepository: UserRepository) {

    @GetMapping("/")
    fun findAll() = articleRepository.findAllByOrderByAddedAtDesc()

    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/{id}")
    fun findOne(@PathVariable id: String): Article =
        articleRepository.findById(id.toLong()).getOrElse { throw ArticleNotFoundException(id) }

    @PostMapping("/")
    fun createArticle(@RequestBody body: Map<String, String>, principal: Principal): ResponseEntity<String> {
        val article = Article(
            body.getOrElse("title") { throw InvalidRequestException("Invalid title") },
            body.getOrElse("headline") { throw InvalidRequestException("Invalid headline") },
            body.getOrElse("content") { throw InvalidRequestException("Invalid content") },
            userRepository.findByUsername(principal.name) ?: throw UserNotFoundException(principal.name),
        )
        articleRepository.save(article)
        return ResponseEntity.status(HttpStatus.CREATED).body("Created article successfully")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @PutMapping("/{id}")
    fun editArticle(
        @PathVariable id: String,
        @RequestBody body: Map<String, String>,
        principal: Principal
    ): ResponseEntity<String> {
        val foundArticle = articleRepository.findById(id.toLong()).getOrElse { throw ArticleNotFoundException(id) }
        if (foundArticle.author.username != principal.name) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        foundArticle.title = body.getOrDefault("title", foundArticle.title)
        foundArticle.headline = body.getOrDefault("headline", foundArticle.headline)
        foundArticle.content = body.getOrDefault("content", foundArticle.content)
        articleRepository.save(foundArticle)
        return ResponseEntity.status(HttpStatus.OK).body("Updated article successfully")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: String, principal: Principal): ResponseEntity<String> {
        val foundArticle = articleRepository.findById(id.toLong()).getOrElse { throw ArticleNotFoundException(id) }
        if (foundArticle.author.username != principal.name) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        articleRepository.deleteById(foundArticle.id!!)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/user")
class UserController(private val userRepository: UserRepository) {
    @GetMapping("/")
    fun getCurrentUser(principal: Principal) = userRepository.findByUsername(principal.name)?.username
}

@Controller
class GreetingController {
    @MessageMapping("/sendmessage")
    @SendTo("/topic/chatroom")
    fun greeting(message: Message): Message {
        Thread.sleep(200) // simulated delay
        return Message(HtmlUtils.htmlEscape(message.content!!))
    }
}