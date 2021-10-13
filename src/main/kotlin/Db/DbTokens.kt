package Db

import Db.Tables.TokensTable
import Models.Token
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DbTokens {

    fun get(id: Int): Token? {
        var token: Token? = null
        transaction {
            TokensTable.select { TokensTable.id.eq(id) }.forEach {
                token = TableToData(it)
            }
        }
        return token
    }

    fun getByRaw(raw: String): Token? {
        var token: Token? = null
        transaction {
            TokensTable.select { TokensTable.raw.eq(raw) }.forEach {
                token = TableToData(it)
            }
        }
        return token
    }

    fun insert(token: Token): Int? {
        var uid: Int? = null
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                uid = TokensTable.insert {
                    it[user] = token.user
                    it[raw] = token.raw
                    it[enabled] = token.enabled
                } get TokensTable.id
            }
        } catch (e: Exception) {
            println("Exception Error")
            println(e.message)
        }
        return uid
    }

    private fun TableToData(resultRow: ResultRow): Token {
        return Token(
            resultRow[TokensTable.id],
            resultRow[TokensTable.user],
            resultRow[TokensTable.raw],
            resultRow[TokensTable.enabled],
        )
    }

}