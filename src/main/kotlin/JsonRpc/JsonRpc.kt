package JsonRpc

import com.google.gson.Gson
import mu.KotlinLogging
import org.json.JSONArray
import org.json.JSONObject

class JsonRpc {

    private data class _RequestData(
        val jsonrpc: String,
        val method: String,
        val params: Any,
        val id: Int,
    )

    data class Result(var result: JSONObject? = null, var error: ErrorData? = null)

    private val logger = KotlinLogging.logger {}

    private val ids = mutableMapOf<String, Result>()

    fun parse(strJson: String): RequestData? {
        val gson = Gson()
        try {
             gson.fromJson(strJson, _RequestData::class.java)?.let {
                 if (it.jsonrpc =="2.0" && it.id>0 && it.method!=null) {
                     val params = gson.toJson(it.params)
                     return RequestData(jsonrpc = it.jsonrpc, method = it.method, params = params, id = it.id)
                 }
             }
        } catch ( e: Exception) {
            logger.error { "Exception $e"}
        }
        return null
    }

    fun setError(id: Int, error: ErrorData) = setError(id.toString(), error)

    fun setError(id: String, error: ErrorData) {
        ids[id]?.let {
            ids[id] = it.copy(error = error)
        }?:run {
            ids[id] = Result(error = error)
        }
    }
    fun setResult(id: Int, json: JSONObject) = setResult(id.toString(), json)
    fun setResult(id: String, json: JSONObject) {
        ids[id]?.let {
            ids[id] = it.copy(result = json)
        }?:run {
            ids[id] = Result(result = json)
        }
    }

    fun getString(): String? {
        val jsons = JSONArray()
        ids.forEach { id, result ->

            val json = JSONObject()
            json.put("jsonrpc", "2.0")

            json.put("id", id)
            result.error?.let {
                val jsonResult = JSONObject()
                jsonResult.put("code", it.code)
                jsonResult.put("message", it.message)
                json.put("error", jsonResult)
            } ?: run {
                result.result.let {
                    json.put("result", it)
                } ?: run {
                    val jsonResult = JSONObject()
                    jsonResult.put("code", -32603)
                    jsonResult.put("message", "Internal error")
                    json.put("error", jsonResult)
                }
            }
            jsons.put(json)
        }

        if (ids.count() == 1) {
            return jsons.getJSONObject(0).toString()
        } else if (ids.count() > 1){
            return jsons.toString()
        } else {
            return null
        }
    }

}