package de.complimentaryapp.server

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select

object Friends : Table() {
    val a = reference("a", Users.id)
    val b = reference("b", Users.id)

    fun areFriends(a: String, b: String): Boolean {
        return DatabaseController.call {
            Friends.select { ((Friends.a eq a) and (Friends.b eq b)) or ((Friends.b eq a) and (Friends.a eq b)) }
                .empty().not()
        }
    }

    fun getFriends(a: String): List<String> {
        return DatabaseController.call {
            Friends.select { (Friends.a eq a) or (Friends.b eq a) }
                .map { if (it[Friends.a] == a) it[Friends.b] else it[Friends.a] }
                .toList()
        }
    }
}
