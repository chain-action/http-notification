package Db.Tables

import Db.Tables.UsersTable.autoIncrement
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TokensTable : Table("tokens") {
    val id = integer("id").autoIncrement()
    val user = reference("user", UsersTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val raw = varchar("raw", 255)
    val enabled = bool("enabled")
    init {
        PrimaryKey(id)
    }
}