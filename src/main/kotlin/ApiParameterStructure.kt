import Enums.ItemDefault
import Models.ParameterStructure
import io.ktor.util.*
import org.json.JSONArray
import org.json.JSONObject

class ApiParameterStructure {
//    private var json = JSONObject()

    init {
//        val list = JSONObject()
        var json = JSONObject()
//        json.put("list", list)
        json.put("success", true)

//        val parameterStructure = ParameterStructure()
        val listParam = listOf(
            ParameterStructure("url", "string", "description..."),
            ParameterStructure("method", "list", "description...",false, ItemDefault.METHOD.value),
            ParameterStructure("query", "list", "description...",false, ItemDefault.QUERY.value),
        )
        json.put("list", listParam)

      /*  val optionsLibrary = listOf<ParameterStructure>(
            ParameterStructure("url", "string", "description..."),
            ParameterStructure("url", "list", "description..."),
        )     */

    }

    private fun getLibs(): JSONArray {
        val types =  listOf("string", "int", "list", "object" )
        val json = JSONArray(types)
        return json
    }
    private fun getLists(): JSONObject {
        val json = JSONObject()
//        json.put("types", listOf("string", "int", "list", "object" ) )
        json.put("method",  listOf("GET", "PUT", "POST", "JSONRPC" ))
        return json
    }

    fun getJsonV0(): JSONObject {
        val json = JSONObject()
        val listParam = listOf(
            ParameterStructure("hash", "string", "Unique hash of the event "),
            ParameterStructure("data", "string", "Url in base64"),
//            ParameterStructure("method", "list", "List of available http methods ",false, ItemDefault.METHOD.value),
//            ParameterStructure("query", "list", "Parameter name",false, ItemDefault.QUERY.value),
        )
        json.put("list", listParam)
        json.put("library", getLibs())
        json.put("success", true)
        return json
    }
    fun getJsonV1(): JSONObject {
        val json = JSONObject()
        val listParam = listOf(
            ParameterStructure("hash", "string", "Unique hash of the event "),
            ParameterStructure("url", "string", "url address"),
            ParameterStructure("method", "list", "List of available http methods ",false, ItemDefault.METHOD.value),
            ParameterStructure("query", "list", "Parameter name",false, ItemDefault.QUERY.value),
            ParameterStructure("auth", "object", "Authorization object",false, null),
        )
        json.put("list", listParam)
        val library = JSONObject()
        library.put("types", getLibs())
        library.put("list", getLists())
        json.put("library", library)
        json.put("success", true)
        return json
    }
}