package com.example.blog

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/article")
class ArticleController(private val articleRepository: ArticleRepository, private val userRepository: UserRepository) {

    @GetMapping("/")
    fun findAll() = articleRepository.findAllByOrderByAddedAtDesc()

    @GetMapping("/{slug}")
    fun findOne(@PathVariable slug: String) =
        articleRepository.findBySlug(slug) ?: throw ArticleNotFoundException(slug)

    @PostMapping("/article")
    fun createArticle(@RequestBody article: Article): ResponseEntity<String> {
        article.author =
            userRepository.findByLogin(article.author.login) ?: throw UserNotFoundException(article.author.login)
        articleRepository.save(article)
        return ResponseEntity.status(HttpStatus.CREATED).body("Created article successfully")
    }

    @PutMapping("/article/{slug}")
    fun editArticle(
        @PathVariable slug: String,
        @RequestBody body: Map<String, String>
    ): ResponseEntity<String> {
        val foundArticle = articleRepository.findBySlug(slug)
        if (foundArticle == null) {
            throw ArticleNotFoundException(slug)
        }
        foundArticle.title = body.getOrDefault("title", foundArticle.title)
        foundArticle.headline = body.getOrDefault("headline", foundArticle.headline)
        foundArticle.content = body.getOrDefault("content", foundArticle.content)
        articleRepository.save(foundArticle)
        return ResponseEntity.status(HttpStatus.OK).body("Updated article successfully")
    }

    @DeleteMapping("/article/{slug}")
    fun deleteArticle(@PathVariable slug: String): ResponseEntity<String> {
        val foundArticle = articleRepository.findBySlug(slug)
        if (foundArticle == null || foundArticle.id == null) {
            throw ArticleNotFoundException(slug)
        }
        foundArticle.id?.let { articleRepository.deleteById(it) }
        return ResponseEntity.status(HttpStatus.OK).body("Deleted article successfully")
    }
}

@RestController
@RequestMapping("/api/user")
class UserController(private val repository: UserRepository) {

    @GetMapping("/")
    fun findAll() = repository.findAll()

    @GetMapping("/{login}")
    fun findOne(@PathVariable login: String) =
        repository.findByLogin(login) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not exist")
}