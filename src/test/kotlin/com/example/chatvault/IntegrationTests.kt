package com.example.chatvault

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    @BeforeAll
    fun setup() {
        println(">> Setup")
    }

    @Test
    fun `Assert blog page title, content and status code`() {
        println(">> Assert blog page title, content and status code")
        val entity = restTemplate.getForEntity<String>("/")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("<title>Kotlin&#39;s blog</title>", "Lorem")
    }

    @WithMockUser(username="tester", roles=["USER"])
    @Test
    fun `Assert article page title, content and status code`() {
        println(">> Assert article page title, content and status code")
        val title = "Lorem"
        val entity = restTemplate.getForEntity<String>("/article/1")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains(title, "Lorem", "dolor sit amet")
    }

    @AfterAll
    fun teardown() {
        println(">> Tear down")
    }

}