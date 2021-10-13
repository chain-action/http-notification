package Db.Tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ItemsTable : Table("items") {
//    val id = integer("id").autoIncrement()
    val uid = varchar("uid", 255)
    val user = reference("user", UsersTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val method = integer("method")
    val url = varchar("url", 255)
    val query = varchar("query", 255).nullable()
    val auth = varchar("auth", 255).nullable()

    init {
        PrimaryKey(uid)
//        index(true, id, tg_user) // case 1 - Unique index
    }
}