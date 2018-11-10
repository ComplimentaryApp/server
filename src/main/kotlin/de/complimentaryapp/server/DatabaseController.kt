package de.complimentaryapp.server

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DatabaseController {
    private var connected = false

    private fun connect() {
        if (connected) return
        Database.connect(System.getenv("JDBC_DATABASE_URL"), "org.postgresql.Driver",
            user = System.getenv("JDBC_DATABASE_USERNAME"), password = System.getenv("JDBC_DATABASE_PASSWORD"))
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        connected = true
    }

    fun <T> call(statement: Transaction.() -> T): T {
        connect()
        return transaction { statement() }
    }
}

fun main(args: Array<String>) {
    DatabaseController.call {
        SchemaUtils.drop(Users, Sessions, Compliments)
        SchemaUtils.create(Users, Sessions, Compliments)
    }
}
