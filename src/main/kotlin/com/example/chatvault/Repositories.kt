package com.example.chatvault

import org.springframework.data.repository.CrudRepository

interface ArticleRepository: CrudRepository<Article, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}

interface UserRepository: CrudRepository<Users, Long> {
    fun findByUsername(username: String): Users?
}
