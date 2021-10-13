package Db.Tables

import org.jetbrains.exposed.sql.Table

object UsersTable: Table("users") {
    val id = integer("id").autoIncrement()
    val enabled = bool("enabled")
    init {
        PrimaryKey(id)
    }
}

