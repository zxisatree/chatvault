package com.zxisatree.chatvault.vault

import com.zxisatree.chatvault.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.security.Principal
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/article")
class ArticleController(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) {
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
        if (foundArticle.author.username != principal.name) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .build()
        foundArticle.title = body.getOrDefault("title", foundArticle.title)
        foundArticle.content = body.getOrDefault("content", foundArticle.content)
        articleRepository.save(foundArticle)
        return ResponseEntity.status(HttpStatus.OK).body("Updated article successfully")
    }

    @OptIn(ExperimentalStdlibApi::class)
    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: String, principal: Principal): ResponseEntity<String> {
        val foundArticle = articleRepository.findById(id.toLong()).getOrElse { throw ArticleNotFoundException(id) }
        if (foundArticle.author.username != principal.name) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .build()
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

@RestController
@RequestMapping("/api/admin")
class AdminController(private val userRepository: UserRepository) {
    @GetMapping("/", produces=[MediaType.TEXT_PLAIN_VALUE])
    fun getLogs(): String {
        return File("logs/chatvault.log").readText()
    }
}

@RestController
@RequestMapping("/api/room")
class RoomController(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val repositoryUtilities: RepositoryUtilities) {
    @GetMapping("/")
    fun getRooms(): Iterable<Room> = roomRepository.findAll()

    @GetMapping("/{id}")
    fun getRoom(@PathVariable id: String) = roomRepository.findByIdOrNull(id.toLongOrNull() ?: throw Exception("id is not an integer")) ?: throw Exception ("room $id not found")

    @PostMapping("/")
    fun createRoom(@RequestBody body: Map<String, String>, principal: Principal): ResponseEntity<String> {
        val creator = userRepository.findByUsername(principal.name) ?: throw Exception("Current user not found in DB")
        val name = body["name"] ?: throw Exception("The new server needs a name field.")
        repositoryUtilities.createRoom(name, creator)
        return ResponseEntity.status(HttpStatus.CREATED).body("Created room successfully")
    }

    @DeleteMapping("/{id}")
    fun deleteRoom(@PathVariable id: String, principal: Principal) {
        // Check if the current user is an admin of the room
        val idNumber = id.toLongOrNull() ?: throw Exception("room ID not an integer")
        val user = userRepository.findByUsername(principal.name) ?: throw Exception("Current user not found in DB")
        val room = roomRepository.findByIdOrNull(idNumber) ?: throw Exception("room $id not found")
        if (user !in room.admins) {
            throw Exception("current user is not an admin of room $id")
        }
        repositoryUtilities.deleteRoom(room)
    }
}