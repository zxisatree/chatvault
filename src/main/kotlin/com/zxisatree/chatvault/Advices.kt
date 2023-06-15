package com.zxisatree.chatvault

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class Advices {
    @ResponseBody
    @ExceptionHandler(ArticleNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun articleNotFoundHandler(ex: ArticleNotFoundException): String? {
        return ex.message
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun userNotFoundHandler(ex: UserNotFoundException): String? {
        return ex.message
    }

    @ResponseBody
    @ExceptionHandler(InvalidRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalidRequestHandler(ex: InvalidRequestException): String? {
        return ex.message
    }

    @ResponseBody
    @ExceptionHandler(ChatroomDoesNotExistException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun chatroomDoesNotExistHandler(ex: ChatroomDoesNotExistException): String? {
        return ex.message
    }

    @ResponseBody
    @ExceptionHandler(InvalidMessageException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun chatroomDoesNotExistHandler(ex: InvalidMessageException): String? {
        return ex.message
    }
}