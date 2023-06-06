package com.zxisatree.chatvault

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

interface IMessage {
    val isServerMessage: Boolean
    val content: String
}

data class Message(override val content: String, val username: String): IMessage {
    override val isServerMessage: Boolean = false
}

data class ServerMessage(override val content: String): IMessage {
    override val isServerMessage: Boolean = true
}