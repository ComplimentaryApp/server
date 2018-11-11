package de.complimentaryapp.server

import org.jetbrains.exposed.sql.Table

object Compliments : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val author = reference("author", Users.id).nullable()
    val subject = reference("subject", Users.id)
    val body = text("body")
    val liked = bool("liked").default(false)
    val positivity = double("positivity").default(0.5)
    val sadness = bool("sadness")
    val incompetence = bool("incompetence")
    val stress = bool("stress")
    val loneliness = bool("loneliness")
}
