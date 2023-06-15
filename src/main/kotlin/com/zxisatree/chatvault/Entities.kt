package com.zxisatree.chatvault

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Article(
    var title: String,
    var content: String,
    @ManyToOne var author: User,
    var addedAt: LocalDateTime = LocalDateTime.now(),
    @Id @GeneratedValue var id: Long? = null
)

@Entity
@Table(name = "users")
class User(
    var username: String,
    @Column(nullable = false) var password: String,
    @Column(nullable = false) var enabled: Boolean,
    @ManyToMany(mappedBy = "admins", fetch = FetchType.EAGER) @JsonBackReference var adminIn: MutableSet<Room>,
    @Id @GeneratedValue var id: Long? = null
)

@Entity
class Room(
    var name: String,
    @ManyToMany(fetch = FetchType.EAGER) @JsonManagedReference var admins: MutableSet<User>,
    @Id @GeneratedValue var id: Long? = null
)

interface IMessage {
    val isServerMessage: Boolean
    val content: String
}

data class Message(override val content: String, val username: String) : IMessage {
    override val isServerMessage: Boolean = false
}

data class ServerMessage(override val content: String) : IMessage {
    override val isServerMessage: Boolean = true
}
