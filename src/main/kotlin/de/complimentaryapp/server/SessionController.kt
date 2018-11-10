package de.complimentaryapp.server

import de.complimentaryapp.server.Sessions.TOKEN_LENGTH
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.RuntimeException
import java.security.SecureRandom
import kotlin.streams.asSequence

@RestController
class SessionController {

    @PostMapping("/login")
    fun login(@RequestParam username: String): String {
        if (!Users.checkUser(username)) throw RuntimeException("Bad username")
        while (true) {
            val token = token()
            println(token)
            try {
                DatabaseController.call {
                    Sessions.insert {
                        it[Sessions.user] = username
                        it[Sessions.token] = token()
                    }
                }
                return token
            } catch (e: ExposedSQLException) {
                e.printStackTrace()
                if (e.message?.contains(Regex("UNIQUE constraint failed:.+token")) == true) {
                    continue
                } else throw e
            }
        }
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader(name = "Token") token: String) {
        val user = Sessions.checkToken(token) ?: throw RuntimeException("No/Bad token provided")
        DatabaseController.call {
            Sessions.deleteWhere { Sessions.token eq token }
        }
    }

    private fun token(): String {
        val random = SecureRandom()
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
        return random.ints(TOKEN_LENGTH.toLong(), 0, source.length).asSequence().map { source[it] }.joinToString("")
    }
}
