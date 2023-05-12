package com.example.blog

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    fun createArticle(@RequestBody article: Article): ResponseEntity<String> {
        article.author =
            userRepository.findByLogin(article.author.login) ?: throw UserNotFoundException(article.author.login)
        articleRepository.save(article)
        return ResponseEntity.status(HttpStatus.CREATED).body("Created article successfully")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @PutMapping("/{id}")
    fun editArticle(
        @PathVariable id: String,
        @RequestBody body: Map<String, String>
    ): ResponseEntity<String> {
        val foundArticle = articleRepository.findById(id.toLong()).getOrElse { throw ArticleNotFoundException(id) }
        foundArticle.title = body.getOrDefault("title", foundArticle.title)
        foundArticle.headline = body.getOrDefault("headline", foundArticle.headline)
        foundArticle.content = body.getOrDefault("content", foundArticle.content)
        articleRepository.save(foundArticle)
        return ResponseEntity.status(HttpStatus.OK).body("Updated article successfully")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: String): ResponseEntity<String> {
        val foundArticle = articleRepository.findById(id.toLong()).getOrElse { throw ArticleNotFoundException(id) }
        articleRepository.deleteById(foundArticle.id!!)
        return ResponseEntity.noContent().build()
    }
}

//@RestController
//@RequestMapping("/api/user")
//class UserController(private val userRepository: UserRepository) {
//}
