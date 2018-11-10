package de.complimentaryapp.server

import org.jetbrains.exposed.sql.Table

object Compliments : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val author = reference("author", Users.id)
    val subject = reference("subject", Users.id)
    val body = text("body")
    val liked = bool("liked").default(false)
}
