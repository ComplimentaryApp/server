package de.complimentaryapp.server

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@RestController
class UserController {

    @PutMapping("/users")
    fun put(@RequestParam username: String, @RequestParam firstName: String, @RequestParam lastName: String,
                 @RequestParam email: String, @RequestParam birth: String, @RequestParam gender: Char) {
        if (!validDate(birth)) throw RuntimeException("Bad date")
        if (!Regex("[a-zA-Z0-9+._\\%-+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+")
                .matches(email)) throw RuntimeException("Bad email")
        DatabaseController.call {
            try {
                Users.insert {
                    it[Users.id] = username
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

    fun parseDate(date: String): Date? {
        return try {
            SimpleDateFormat("YYYYMMDD").parse(date)
        } catch (e: ParseException) {
            null
        }
    }

    fun validDate(date: String): Boolean {
        return parseDate(date) != null
    }
}
