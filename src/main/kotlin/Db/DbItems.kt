package Db

import Db.Tables.ItemsTable
import Enums.ItemDefault
import Enums.Method
import Models.Item
import Models.Raw.ItemRaw
import Utils.UtilAuth
import Utils.UtilEnum
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DbItems {

    private var enumMethod: UtilEnum
//    private val utilQuery= UtilQuery()
    private val utilAuth= UtilAuth()

    init {
        enumMethod = UtilEnum()
        Method.values().forEach { enumMethod.put(it.id, it.name) }
    }

    fun getRaw(uid: String): ItemRaw? {
        var itemRaw: ItemRaw? = null
        transaction {
            ItemsTable.select { ItemsTable.uid.eq(uid) }.forEach {
                itemRaw = TableToData(it)
            }
        }
        return itemRaw
    }

    fun getRaw(uid: String, userId: Int): ItemRaw? {
        var itemRaw: ItemRaw? = null
        transaction {
            ItemsTable.select { ItemsTable.uid.eq(uid) and ItemsTable.user.eq(userId) }.forEach {
                itemRaw = TableToData(it)
            }
        }
        return itemRaw
    }

    fun get(uid: String) = convertItem(getRaw(uid))

    fun get(uid: String, userId: Int) = convertItem(getRaw(uid, userId))

    private fun convertItem(itemRaw: ItemRaw?): Item?{
        if (itemRaw != null) {
            enumMethod.getName(itemRaw.method)?.let { it1 ->
                Method.valueOf(it1).let { method ->
                    val auth = utilAuth.get(itemRaw.auth)
                    val query = itemRaw.query ?: ItemDefault.QUERY.value
                    return Item(itemRaw.uid, itemRaw.user, method, itemRaw.url, query, auth) //@TODO("itemRaw.query")
                }
            }
        }
        return null
    }

    private fun convertItem(item: Item?): ItemRaw? {
        if (item != null) {
            val auth = utilAuth.get(item.auth)
            return ItemRaw(item.uid, item.user, item.method.id, item.url, item.query, auth)
        }
        return null
    }
    fun insert(item: Item) = convertItem(item)?.let { insert(it) }

    fun insert(itemRaw: ItemRaw): String? {
        var _uid: String? = null
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                _uid = ItemsTable.insert {
                    it[uid] = itemRaw.uid
                    it[user] = itemRaw.user
                    it[method] = itemRaw.method
                    it[url] = itemRaw.url
                    it[query] = itemRaw.query
                    it[auth] = itemRaw.auth
                } get ItemsTable.uid
            }
        } catch (e: Exception) {
            println("Exception Error")
            println(e.message)
        }
        return _uid
    }

    private fun TableToData(resultRow: ResultRow): ItemRaw {
        return ItemRaw(
            resultRow[ItemsTable.uid],
            resultRow[ItemsTable.user],
            resultRow[ItemsTable.method],
            resultRow[ItemsTable.url],
            resultRow[ItemsTable.query],
            resultRow[ItemsTable.auth],
        )
    }
}