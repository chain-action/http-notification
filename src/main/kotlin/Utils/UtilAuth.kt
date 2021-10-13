package Utils

import Models.Auth
import com.google.gson.Gson

class UtilAuth {
    private val gson = Gson()

    fun get(jsonAuth: String?): Auth? {
        try {
            return gson.fromJson(jsonAuth, Auth::class.java)
        } catch ( e: Exception) {
            println("Exception $e")
            return null
        }

    }
    fun get(auth: Auth?): String? {
        try {
            val json = gson.toJson(auth)
            return json
        } catch ( e: Exception) {
            println("Exception $e")
            return null
        }

    }

}