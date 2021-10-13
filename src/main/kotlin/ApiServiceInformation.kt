import Config.App
import Enums.ItemDefault
import Models.ParameterStructure
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import org.json.JSONObject

class ApiServiceInformation(configYaml: Config?=null) {

    private val configApp = configYaml ?: com.uchuhimo.konf.Config { addSpec(App) }.from.yaml.file("./config.yml")

    fun getJsonV0(): JSONObject {
        val json = JSONObject()
        val listParam = mapOf(
            "title" to configApp[App.info_title],
            "descr" to configApp[App.info_descr],
            "logo" to configApp[App.info_logo],
            "support_surf" to configApp[App.info_support_surf],
        )
        json.put("info", listParam)
        json.put("success", true)
        return json
    }
    fun getJsonV1()=getJsonV0()
}