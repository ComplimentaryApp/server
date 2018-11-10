package de.complimentaryapp.server

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object Sessions: Table() {
    val TOKEN_LENGTH = 32
    val user = reference("user", Users.id)
    val token = varchar("token", TOKEN_LENGTH).uniqueIndex()

    fun checkToken(token: String): String? {
        return DatabaseController.call {
            Sessions.select { Sessions.token eq token }.limit(1).firstOrNull()?.get(Sessions.user)
        }
    }
}
