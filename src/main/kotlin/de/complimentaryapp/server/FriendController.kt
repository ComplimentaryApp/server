package de.complimentaryapp.server

import org.jetbrains.exposed.sql.*
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException

@RestController
class FriendController {
    @PostMapping("/friends")
    fun addFriend(@RequestParam subject: String, @RequestHeader(name = "Token") token: String?) {
        val user = token?.let { Sessions.checkToken(it) } ?: throw RuntimeException("No/Bad token provided")
        if (!Users.checkUser(subject)) throw RuntimeException("Bad friend")
        if (!Friends.areFriends(user, subject)) {
            DatabaseController.call {
                Friends.insert {
                    it[Friends.a] = user
                    it[Friends.b] = subject
                }
            }
        }
    }
    @PostMapping("/friends")
    fun myFriends(@RequestHeader(name = "Token") token: String?) {
        val user = token?.let { Sessions.checkToken(it) } ?: throw RuntimeException("No/Bad token provided")

        data class Friend(val id: String, val first: String, val last: String)

        return DatabaseController.call {
            Friends.getFriends(user).map { id ->
                Users.select { Users.id eq id }
                    .map { Friend(it[Users.id], it[Users.firstName], it[Users.lastName]) }
                    .first()
            }
        }
    }
    @DeleteMapping("/friends")
    fun removeFriend(@RequestParam subject: String, @RequestHeader(name = "Token") token: String?) {
        val user = token?.let { Sessions.checkToken(it) } ?: throw RuntimeException("No/Bad token provided")
        if (!Users.checkUser(subject)) throw RuntimeException("Bad friend")
        if (Friends.areFriends(user, subject)) {
            DatabaseController.call {
                Friends.deleteWhere { ((Friends.a eq user) and (Friends.b eq subject)) or
                        ((Friends.b eq user) and (Friends.a eq subject)) }
            }
        }
    }
}
