package de.complimentaryapp.server

import org.jetbrains.exposed.sql.Table

object Sessions: Table() {
    val TOKEN_LENGTH = 32
    val user = reference("user", Users.id)
    val token = varchar("token", TOKEN_LENGTH).uniqueIndex()
}
