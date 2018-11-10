package de.complimentaryapp.server

import org.jetbrains.exposed.sql.*

object Users : Table() {
    val id = text("id").primaryKey()
    val email = text("email").uniqueIndex()
    val firstName = text("firstname")
    val lastName = text("lastname")
    val birth = varchar("birth", 8).nullable()
    val gender = char("gender")
}
