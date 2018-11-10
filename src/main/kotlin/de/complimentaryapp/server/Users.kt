package de.complimentaryapp.server

import org.jetbrains.exposed.sql.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

object Users : Table() {
    val id = text("id").primaryKey()
    val email = text("email").uniqueIndex()
    val firstName = text("firstname")
    val lastName = text("lastname")
    val birth = varchar("birth", 8).nullable()
    val gender = char("gender")

    fun parseDate(date: String): Date? {
        return try {
            SimpleDateFormat("YYYYMMDD").parse(date)
        } catch (e: ParseException) {
            null
        }
    }

    fun checkUser(id: String): Boolean {
        return DatabaseController.call { Users.select { Users.id eq id }.count() != 0 }
    }

    fun validDate(date: String): Boolean {
        return parseDate(date) != null
    }
}
