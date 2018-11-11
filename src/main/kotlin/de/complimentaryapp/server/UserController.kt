package de.complimentaryapp.server

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException

@RestController
class UserController {

    @PostMapping("/users")
    fun create(@RequestParam username: String, @RequestParam firstName: String, @RequestParam lastName: String,
                 @RequestParam email: String, @RequestParam birth: String, @RequestParam gender: Char) {
        if (!Users.validDate(birth)) throw RuntimeException("Bad date")
        DatabaseController.call {
            try {
                Users.insert {
                    it[id] = username
                    it[Users.firstName] = firstName
                    it[Users.lastName] = lastName
                    it[Users.email] = email
                    it[Users.birth] = birth
                    it[Users.gender] = gender
                }
            } catch (e: ExposedSQLException) {
                when {
                    e.message?.contains(Regex("UNIQUE constraint failed:.+email")) == true -> throw RuntimeException("Duplicate email")
                    e.message?.contains(Regex("UNIQUE constraint failed:.+id")) == true -> throw RuntimeException("Duplicate username")
                    else -> throw e
                }
            }
            Users.selectAll().forEach {
                print(it)
            }
        }
    }
}
