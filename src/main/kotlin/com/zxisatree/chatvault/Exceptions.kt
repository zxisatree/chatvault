package com.zxisatree.chatvault

class ArticleNotFoundException(name: String) : RuntimeException("Could not find article $name.")

class UserNotFoundException(username: String) : RuntimeException("Could not find user $username.")

class InvalidRequestException(message: String) : RuntimeException(message)

class ChatroomDoesNotExistException(chatroom: String) : RuntimeException("Chatroom $chatroom does not exist.")

class InvalidMessageException(message: String) : RuntimeException(message)