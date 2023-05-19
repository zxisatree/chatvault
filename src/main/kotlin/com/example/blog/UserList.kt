package com.example.blog

class UserList() {
    private val onlineUsers: HashSet<String> = HashSet()

    fun joinServer(userId: String) {
        onlineUsers.add(userId)
    }

    fun leaveServer(userId: String) {
        if (!onlineUsers.contains(userId)) {
            println("User $userId is not connected to the server")
        }
        onlineUsers.remove(userId)
    }

    fun userCount(): Int = onlineUsers.size
}