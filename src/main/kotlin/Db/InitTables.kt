package Db

import Db.Tables.*
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.transactions.transaction

class InitTables {


    fun init() {
        dropAll()
        createAll()
//        deleteAll()
        addUser()
    }

    fun dropAll() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(
                UsersTable,
                TokensTable,
                ItemsTable,
            )
        }
    }

    fun createAll() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(
                UsersTable,
                TokensTable,
                ItemsTable,
                )

        }
    }
    fun deleteAll() {
        transaction {
            addLogger(StdOutSqlLogger)
            UsersTable.deleteAll()
            TokensTable.deleteAll()
            ItemsTable.deleteAll()
        }
    }
    private fun addUser() {
        transaction {
            addLogger(StdOutSqlLogger)
            UsersTable.insert {
                    it[enabled] = true
                } get UsersTable.id
        }
    }
}