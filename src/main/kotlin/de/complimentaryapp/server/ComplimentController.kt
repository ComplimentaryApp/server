package de.complimentaryapp.server

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException

data class Compliment(val subject: String, val body: String, val liked: Boolean) {
}

@RestController
class ComplimentController {
    @PostMapping("/compliments")
    fun newMessage(@RequestParam subject: String, @RequestBody content: String,
                   @RequestHeader(name = "Token") token: String?) {
        val user = token?.let { Sessions.checkToken(it) } ?: throw RuntimeException("No/Bad token provided")
        if (!Users.checkUser(subject)) throw RuntimeException("Bad friend")
        if (!Friends.areFriends(user, subject)) throw RuntimeException("Not friends")
        DatabaseController.call {
            Compliments.insert {
                it[Compliments.author] = user
                it[Compliments.subject] = subject
                it[Compliments.body] = content
            }
        }
    }

    @GetMapping("/compliments")
    fun messages(@RequestHeader(name = "Token") token: String?): List<Compliment> {
        val user = token?.let { Sessions.checkToken(it) } ?: throw RuntimeException("No/Bad token provided")
        return DatabaseController.call {
            Compliments.select { Compliments.subject eq user }
                .map { Compliment(it[Compliments.author], it[Compliments.author], it[Compliments.liked]) }
        }
    }
}
