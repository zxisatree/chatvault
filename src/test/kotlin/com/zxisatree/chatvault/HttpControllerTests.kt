package com.zxisatree.chatvault

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@WebMvcTest
// To prevent WebSocket controller from being loaded since it requires other beans
@ContextConfiguration(classes = [ArticleController::class])
class HttpControllersTests(@Autowired val mockMvc: MockMvc) {

    // Required for the dependency to be injected
    @MockkBean
    lateinit var userRepository: UserRepository

    @MockkBean
    lateinit var articleRepository: ArticleRepository

    @WithMockUser(username="tester", roles=["USER"])
    @Test
    fun `List articles`() {
        val johnDoe = Users("johnDoe", "password", true)
        val loremArticle = Article("Lorem", "dolor sit amet", johnDoe)
        val ipsumArticle = Article("Ipsum", "dolor sit amet", johnDoe)
        every { articleRepository.findAllByOrderByAddedAtDesc() } returns listOf(loremArticle, ipsumArticle)
        mockMvc.perform(get("/api/article/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].author.username").value(johnDoe.username))
            .andExpect(jsonPath("\$.[0].title").value(loremArticle.title))
            .andExpect(jsonPath("\$.[1].author.username").value(johnDoe.username))
            .andExpect(jsonPath("\$.[1].title").value(ipsumArticle.title))
    }
}