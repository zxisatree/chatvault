package com.example.blog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserRepository,
    val articleRepository: ArticleRepository
) {

    @Test
    fun `When findByIdOrNull then return Article with temporary entries`() {
        val newuser = User("unusedNewUser", "New", "User")
        entityManager.persist(newuser)
        val article = Article("unusedArticle", "Test article", "Lorem ipsum dolor sit amet", newuser)
        entityManager.persist(article) // only works if article.id is nullable and default value is null
        entityManager.flush()
        val found = articleRepository.findByIdOrNull(article.id!!)
        assertThat(found?.title).isEqualTo(article.title)
        assertThat(found?.headline).isEqualTo(article.headline)
        assertThat(found?.content).isEqualTo(article.content)
    }

    @Test
    fun `When findByLogin then return User`() {
        val johnDoe = User("johnDoe", "John", "Doe")
        val user = userRepository.findByLogin(johnDoe.login)
        assertThat(user?.login).isEqualTo(johnDoe.login)
        assertThat(user?.firstname).isEqualTo(johnDoe.firstname)
        assertThat(user?.lastname).isEqualTo(johnDoe.lastname)
    }
}