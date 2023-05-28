package com.example.chatvault

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrElse

@Controller
class HtmlController(
    private val articleRepository: ArticleRepository,
    private val properties: BlogProperties
) {

    @GetMapping("/")
    fun blog(model: Model): String {
        model["title"] = properties.title
        model["banner"] = properties.banner
        model["articles"] = articleRepository.findAllByOrderByAddedAtDesc().map { it.render() }
        return "blog"
    }

    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/article/{id}")
    fun article(@PathVariable id: String, model: Model): String {
        val article = articleRepository
            .findById(id.toLong()).getOrElse { throw ArticleNotFoundException(id) }
            .render()
        model["title"] = article.title
        model["article"] = article
        return "article"
    }

    fun Article.render() = RenderedArticle(
        id!!,
        title,
        content,
        author,
        addedAt.format()
    )

    data class RenderedArticle(
        val id: Long,
        val title: String,
        val content: String,
        val author: Users,
        val addedAt: String
    )

}