package com.example.chatvault

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserRepository,
    val articleRepository: ArticleRepository
) {

    @Test
    fun `When findByIdOrNull then return Article with temporary entries`() {
        val newUser = Users("anonymous", "password", true)
        entityManager.persist(newUser)
        val article = Article("First article", "Lorem ipsum dolor sit amet", newUser)
        entityManager.persist(article) // only works if article.id is nullable and default value is null
        entityManager.flush()
        val found = articleRepository.findByIdOrNull(article.id!!)
        assertThat(found?.title).isEqualTo(article.title)
        assertThat(found?.content).isEqualTo(article.content)
    }

    @Test
    fun `When findByLogin then return User`() {
        val johnDoe = Users("user", "password", true)
        val user = userRepository.findByUsername(johnDoe.username)
        assertThat(user?.username).isEqualTo(johnDoe.username)
        val b = BCryptPasswordEncoder()
        assert(b.matches(johnDoe.password, user?.password))
    }
}