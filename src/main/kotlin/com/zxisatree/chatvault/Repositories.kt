package com.zxisatree.chatvault

import jakarta.transaction.Transactional
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

interface ArticleRepository: CrudRepository<Article, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}

interface UserRepository: CrudRepository<User, Long> {
    fun findByUsername(username: String): User?
}

interface RoomRepository: CrudRepository<Room, Long> {
    fun findByName(name: String): Room?


}

@Component
class RepositoryUtilities(private val articleRepository: ArticleRepository, private val userRepository: UserRepository, private val roomRepository: RoomRepository) {
    @Transactional
    fun createRoom(name: String, creator: User) {
        val newRoom = Room(name, mutableSetOf(creator))
        creator.adminIn.add(newRoom)
        roomRepository.save(newRoom)
        userRepository.save(creator)
    }

    @Transactional
    fun deleteRoom(room: Room) {
        for (user in room.admins) {
            user.adminIn.remove(room)
            userRepository.save(user)
        }
        roomRepository.delete(room)
    }
}
