package com.example.blog

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Article(
    var title: String,
    var content: String,
    @ManyToOne var author: Users,
    var addedAt: LocalDateTime = LocalDateTime.now(),
    @Id @GeneratedValue var id: Long? = null
)

@Entity
class Users(
    @Id var username: String,
    @Column(nullable = false) var password: String,
    @Column(nullable = false) var enabled: Boolean
)

class Message(var username: String?, var content: String?) {
}