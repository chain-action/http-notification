package JsonRpc

import org.json.JSONArray
import org.json.JSONObject

class JsonRpc {

    data class Result(var result: JSONObject? = null, var error: ErrorData? = null)

//    private var errors = mutableMapOf<String, JSONObject>()

//    private val errorMessage = ErrorMessage()

    //    private val results = arrayListOf<JSONObject>()
//    private val results = mutableMapOf<String, JSONObject>()

    private val ids = mutableMapOf<String, Result>()


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