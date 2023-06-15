package com.zxisatree.chatvault

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.zxisatree.chatvault"])
@EnableConfigurationProperties(BlogProperties::class)
class ChatVault

fun main(args: Array<String>) {
    runApplication<ChatVault>(*args)
}
