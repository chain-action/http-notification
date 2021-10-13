package Models.Utils

import Enums.ItemDefault
import Enums.Method
import Models.JsonItem
import Models.Raw.JsonItemRaw
import Utils.UtilEnum
import com.google.gson.Gson
import mu.KotlinLogging

class UtilJsonItem {

    private var enumMethod: UtilEnum
//    private val utilAuth= UtilAuth()

    private val logger = KotlinLogging.logger {}

//    private val gson = Gson()

    init {
        enumMethod = UtilEnum()
        Method.values().forEach { enumMethod.put(it.id, it.name) }
    }

    fun parse(strJson: String): JsonItem? {
        val gson = Gson()
        try {
            val jsonItemRaw = gson.fromJson(strJson, JsonItemRaw::class.java)
            return convertItem(jsonItemRaw)
        } catch ( e: Exception) {
            logger.error { "Exception $e"}
            return null
        }
    }

    private fun convertItem(itemRaw: JsonItemRaw?): JsonItem?{
        if (itemRaw != null) {
            val method = itemRaw.method ?: ItemDefault.METHOD.value
                enumMethod.getNameByName(method)?.let { it1 ->
                Method.valueOf(it1).let { method ->
//                    val auth = utilAuth.get(itemRaw.auth)
                    val query = if (itemRaw.query!=null) itemRaw.query else ItemDefault.QUERY.value
                    return JsonItem(itemRaw.hash, method, itemRaw.url, query, itemRaw.auth)
                }
            }
        }
        return null
    }

/*    private fun convertItem(item: JsonItem?): JsonItemRaw? {
        if (item != null) {
//            val auth = utilAuth.get(item.auth)
            return JsonItemRaw(item.hash, item.method.name, item.url, item.query, item.auth)
        }
        return null
    }*/





}