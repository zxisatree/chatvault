package com.example.blog

class ArticleNotFoundException(slug: String) : RuntimeException("Could not find article $slug") {
}

class UserNotFoundException(username: String) : RuntimeException("Could not find user $username") {
}

class InvalidRequestException(message: String) : RuntimeException(message) {
}