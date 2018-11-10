package de.complimentaryapp.server

import org.jetbrains.exposed.sql.SchemaUtils
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    DatabaseController.call {
        SchemaUtils.create(Users, Sessions, Compliments)
    }

    SpringApplication.run(Application::class.java, *args)
}
