package HttpSender

import Enums.ItemDefault
import Enums.Method
import Models.Auth
import Models.Item
import Utils.UtilEnum
import com.google.gson.Gson
import io.ktor.http.*
import mu.KotlinLogging
import java.io.IOException

class HttpUtil {
    private val logger = KotlinLogging.logger {}

    private val gson = Gson()

    private var enumMethod: UtilEnum

    init {
        enumMethod = UtilEnum()
        Method.values().forEach { enumMethod.put(it.id, it.name) }
    }

    fun getItem(queryParameters: Parameters, userId: Int): Item? {
        val uid = queryParameters["uid"]
        val method = queryParameters["method"].let {
            if (it == null) ItemDefault.METHOD.value
            else
                it
        }
        val url = queryParameters["url"]
        val query = queryParameters["query"].let {
            if (it == null) ItemDefault.QUERY.value
            else
                it
        }
        val auth = getAuth(queryParameters["auth"])
//        val enumMethod = Method.valueOf(method)
        val enumMethod = enumMethod.getNameByName(method)?.let { Method.valueOf(it) }
//        val value = safeValueOf<Method>("test")
        logger.info { "$uid $method $url $query $auth $enumMethod" }

        if (uid!=null && url!=null && enumMethod!=null ) {
            return Item(uid, userId, enumMethod, url, query, auth)
        }
        return null
    }

    private fun getAuth(jsonAuth: String?): Auth? {
        try {
            return gson.fromJson(jsonAuth, Auth::class.java)
        } catch ( e: IOException) {
            println("Exception $e")
            return null
        }
    }


}