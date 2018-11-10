package de.complimentaryapp.server

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException

@RestController
class FriendController {
    @PostMapping("/friends")
    fun addFriend(@RequestParam subject: String, @RequestHeader(name = "Token") token: String?) {
        val user = token?.let { Sessions.checkToken(it) } ?: throw RuntimeException("No/Bad token provided")
        if (!Friends.areFriends(user, subject)) {
            DatabaseController.call {
                Friends.insert {
                    it[Friends.a] = user
                    it[Friends.b] = subject
                }
            }
        }
    }
    @DeleteMapping("/friends")
    fun removeFriend(@RequestParam subject: String, @RequestHeader(name = "Token") token: String?) {
        val user = token?.let { Sessions.checkToken(it) } ?: throw RuntimeException("No/Bad token provided")
        if (Friends.areFriends(user, subject)) {
            DatabaseController.call {
                Friends.deleteWhere { ((Friends.a eq user) and (Friends.b eq subject)) or
                        ((Friends.b eq user) and (Friends.a eq subject)) }
            }
        }
    }
}
