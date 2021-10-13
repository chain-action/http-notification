import Config.App
import Config.HttpServer
import Config.MyArgs
import Db.ConnDB
import Db.DbItems
import Db.InitTables
import Enums.ItemDefault
import Enums.Method
import ErrorInData.ErrorEnum
import ErrorInData.UtilError
import HttpSender.HttpUtil
import JsonRpc.JsonRpc
import Models.Item
import Models.JsonItem
import Models.Utils.UtilJsonItem
import Utils.HeaderAuthorization
import Utils.TmpKafkaPayload
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import com.uchuhimo.konf.source.yaml
import com.xenomachina.argparser.ArgParser
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

import org.jetbrains.exposed.sql.Database
import org.json.JSONObject
import org.slf4j.event.Level
import java.util.*
import kotlin.reflect.KType


fun main(args: Array<String>) {

    val logger = KotlinLogging.logger {}
//    logger.info { "Hello World!" }

    var createTables: Boolean = false
    var debug: Boolean
    var verbose: Boolean
    ArgParser(args).parseInto(::MyArgs).run {

        createTables = this.createtable
        debug = this.debug
        verbose = this.verbose
    }

/*    */
    lateinit var database: Database
    ConnDB().getDatabase().let {
        if (it == null) {
            logger.error("ConnDB.getDatabase()")
            System.exit(1)
        } else {
            database = it
        }
    }
    if (createTables /*|| true*/) {


        logger.info { "Notice: Create all tables..." }
        InitTables().init()
        System.exit(0)
    }

    val configHttpServerYaml: com.uchuhimo.konf.Config
    configHttpServerYaml = com.uchuhimo.konf.Config { addSpec(HttpServer) }.from.yaml.file("./config.yml")
    val configAppYaml = com.uchuhimo.konf.Config { addSpec(App) }.from.yaml.file("./config.yml")

    val utilJsonItem = UtilJsonItem()
    val httpUtil = HttpUtil()
    val httpItem = HttpItem()
    val dbItems = DbItems()
    val utilError = UtilError()

    val rightsToPage = RightsToPage(configAppYaml)


    val port = configHttpServerYaml[HttpServer.port]
    val host = configHttpServerYaml[HttpServer.host]
    val cors_hosts = configHttpServerYaml[HttpServer.cors_hosts]

//
//    Test1().testParsePage()
//    Test1().testRightsToPage()

//    Test1().test("https://domain.com/patch?a=1")
//    Test1().test("https://domain.com/patch")
//    Test1().testUtilMathHttpRetry()

//    var encoderBase64 = Base64.getEncoder()
    val decoderBase64 = Base64.getDecoder()
    val apiParameterStructure = ApiParameterStructure()
    val apiServiceInformation = ApiServiceInformation(configAppYaml)
    val process = Process()

    GlobalScope.launch {
        process.consume()
        while (true) {
            delay(10000)
            process.consume(false)
        }
    }

//val tmpPayload = "e706fbf583da993e003246276fb3d60df51dd58e6fe5d9f987c9eec936d1d490 x6Y855Spuuf21yxBceAMGGEzOiflxDNb cvEAlI/hw+pLEZlH3mXF/gw3dzF1POxagGtYks265EuBVrWlvGLdgsTBzfuTe+JXzJIex5866+IUvbhmjnPQCCNiJmPlHNq8Z+Ks02+50Y1H+iugpW9FMWS4JEl0Foi2bK2sKk0SUt2r/0swxyhZdM/UdNGuRU6jJ4W1z+OWTfzzAiIldsE3nyc2u3puTiUCY179myhqM1Fh7axC+qfEB6Wbb4im8fHMntj8sDq/WCQna5txp9DtGjFya6IIEXPUwON/RfORLzGTaePCZakbeVV2Kw5U3dALwqBZuRqzP/jrjdVf2Okm89pvigBYvEk+YW872oeV/Yf/fZwBPDdXQs7fXOqkVLS6Qr3hC1+gHsVH6aTD8G5Q9xY3bgUTE9WUiK59tVkYz9hgE+4+TyCw6YybXjX1oi8xP7+/XxZR1VxHn73fmKm5bOJ6aC7wWLQRS/Q+aRp3xDRQHktmOvQtajxJv+Gjka5n0ejKnwCnHha98K2Hj1OMoP12d9l/iNwL6YyUAbUh3ykM1xed9oprHbiPES7mPvj8tF/OlBGA+Cjn+3owifb4HPGAyfEuuNDTyPPI9XDQsiBUh15+yt7cBvvMyflnk1ilkwRGfsj5LxDH4Z+aT6XfhLx4V5RY4HeyVDt5Ayl6nQ31GNrFIdzBlJN4jZNIiscfmxqNZ4R5uITItlu+ZQt04mkXqAOe220vAZ867YLa5kupDgn6iK9TYlcL6Af9EMgi80jvEJXVM3JjJgqCv1p58X5yhy/c7hSoXEi8DMN1EbnCqakPA8hixRwllWWOZCpUhuJKnm97W5S9c91leK2QIXGtQfZ3yOhCOJTAJbVD3EUMkAk9hlKHv3KBTdJyANg5WAjjXnqayuCzfTspp4Xxk1XN74EluIl6Hsn4JIpH9o85R0Xb4yKj44QH1igv+0XhJdv1A+F/IOlB+9q+HJLkWd2Td3I="
//    logger.info { TmpKafkaPayload().getPair(tmpPayload) }



    embeddedServer(
        Netty,
        port = port,
        host = host
    ) {
        install(CallLogging) {
            level = Level.INFO
            filter { call -> call.request.path().startsWith("/") }
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
//                registerTypeAdapter(PublicKey::class.java, InstanceCreator<PublicKey> { PkTest() // This is called when I add a breakpoint })
            }
            json()
        }
        install(OpenAPIGen) {
            // basic info
            info {
                version = "0.0.1"
                title = "Test API"
                description = "The Test API"
                contact {
                    name = "Support"
                    email = "support@test.com"
                }
            }
            // describe the server, add as many as you want
            server("http://$host:$port/") {
                description = "Test server"
            }
            //optional custom schema object namer
            replaceModule(DefaultSchemaNamer, object : SchemaNamer {
                val regex = Regex("[A-Za-z0-9_.]+")
                override fun get(type: KType): String {
                    return type.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
                }
            })
        }
//        install(ForwardedHeaderSupport)
//        install(DefaultHeaders)
        install(CORS) {
//            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Options)
//            method(HttpMethod.Put)
//            method(HttpMethod.Delete)
//            method(HttpMethod.Patch)
//            header(HttpHeaders.Authorization)
//            header("MyCustomHeader")
//            header(HttpHeaders.AccessControlAllowHeaders)
            header(HttpHeaders.ContentType)
//            header(HttpHeaders.AccessControlAllowCredentials)
//            header(HttpHeaders.Origin)
            header(HttpHeaders.AccessControlAllowOrigin)
            allowCredentials = true

            cors_hosts?.forEach {
                it.trim().let {
                    if (it == "*")
                        anyHost()
                    else
                        host(it)
                }
            }
        }

        routing {
            static {
                preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP) {
                    resources("static")
                }
            }
            get("/_openapi.json") {
                call.respond(application.openAPIGen.api.serialize())
            }
            get("/") {
                call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
            }

            post("/v0/jsonrpc") {
//                val strJson = call.receive<String>()
//                val strJson =""
                val parameters = call.receiveParameters()
                val getHash = parameters["hash"]
                val getData = parameters["data"]
                val jsonRpc = JsonRpc()
                val userId = 1
                val requestId = 1
                var jsonItem: JsonItem?= null
                logger.info { "hash = $getHash, data = $getData" }
                if (getHash != null && getData != null) {
                    try {
                        val receiver = String(decoderBase64.decode(getData))
                        logger.info { "receiver = $receiver" }
                        jsonItem = JsonItem(
                            hash = getHash, method = Method.POST, url = receiver,
                            query = ItemDefault.QUERY.value, auth = null
                        )
                    } catch (e: Exception) {
                        logger.error { "Exception $e" }
                        jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.Parse))
                    }

                } else {
                    jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.Parse))
                }

                if (jsonItem!=null) { //jsonItem ->

                    logger.info { "jsonItem = $jsonItem" }

                    jsonItem.url.toHttpUrlOrNull()?.let {
                        val item: Item = getItem(jsonItem, userId)

                        if(rightsToPage.get(jsonItem.url)) {
                            dbItems.insert(item)?.let {
                                val json = JSONObject()
                                json.put("success", true)
                                jsonRpc.setResult(requestId, json)
                            } ?: run {
                                dbItems.get(item.uid)?.let {
                                    jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.EntryExists))
                                } ?: run {
                                    jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.Unknown))
                                }
                            }
                        } else {
                            jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.NoConfirmationRightsURL))
                        }

                    }?: run {
                        jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.AddressNotCorrect))
                    }

                } else {
                    jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.Parse))
                }

//                val parameters = call.request.queryParameters

                val result = jsonRpc.getString()
                if (result != null) {
                    call.respondText(result, ContentType.Application.Json, HttpStatusCode.OK)
                } else {
                    call.respondText("{}", ContentType.Application.Json, HttpStatusCode.BadRequest)
                }
            }

            get("/v0/structure") {
                val jsonRpc = JsonRpc()
                jsonRpc.setResult(1, apiParameterStructure.getJsonV0())
                jsonRpc.getString()?.let {
                    call.respondText(it, ContentType.Application.Json, HttpStatusCode.OK)
                } ?: run {
                    call.respondText("{}", ContentType.Application.Json, HttpStatusCode.BadRequest)
                }
            }
            get("/v1/structure") {
                val jsonRpc = JsonRpc()
                jsonRpc.setResult(1, apiParameterStructure.getJsonV1())
                jsonRpc.getString()?.let {
                    call.respondText(it, ContentType.Application.Json, HttpStatusCode.OK)
                } ?: run {
                    call.respondText("{}", ContentType.Application.Json, HttpStatusCode.BadRequest)
                }
            }
            get("/v0/info") {
                val jsonRpc = JsonRpc()
                jsonRpc.setResult(1, apiServiceInformation.getJsonV0())
                jsonRpc.getString()?.let {
                    call.respondText(it, ContentType.Application.Json, HttpStatusCode.OK)
                } ?: run {
                    call.respondText("{}", ContentType.Application.Json, HttpStatusCode.BadRequest)
                }
            }
            post("/v1/jsonrpc") {
                val strJson = call.receive<String>()
//                logger.info { "strJson = $strJson" }

                val userId = 1
                val requestId = 1

                val jsonRpc = JsonRpc()

                utilJsonItem.parse(strJson)?.let { jsonItem ->

                    logger.info { "jsonItem = $jsonItem" }

                    jsonItem.url.toHttpUrlOrNull()?.let {
                        val item: Item = getItem(jsonItem, userId)

                        if(rightsToPage.get(jsonItem.url)) {
                            dbItems.insert(item)?.let {
                                val json = JSONObject()
                                json.put("success", true)
                                jsonRpc.setResult(requestId, json)
                            } ?: run {
                                dbItems.get(item.uid)?.let {
                                    jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.EntryExists))
                                } ?: run {
                                    jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.Unknown))
                                }
                            }
                        } else {
                            jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.NoConfirmationRightsURL))
                        }

                    }?: run {
                        jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.AddressNotCorrect))
                    }

                }?:run {
                    jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.Parse))
                }

//                val parameters = call.request.queryParameters

                val result = jsonRpc.getString()
                if (result != null) {
                    call.respondText(result, ContentType.Application.Json, HttpStatusCode.OK)
                } else {
                    call.respondText("{}", ContentType.Application.Json, HttpStatusCode.BadRequest)
                }
            }

            post("/v0/approve-code") {
                val get_url = call.request.queryParameters["url"]
                val parameters = call.receiveParameters()
                val url = parameters["url"]
                val message = parameters["message"].toBoolean()

                logger.info { "/approve-code $url ${url}" }
                val requestId = 1
                val jsonRpc = JsonRpc()
                url?.let {
                    val approveCode = ApproveCode()
                    approveCode.get(url)?.let {
                        val json = JSONObject(it)
                        json.put("success", true)
                        jsonRpc.setResult(requestId, json)
                    }?: run {
                        jsonRpc.setError(requestId, utilError.getErrorData(ErrorEnum.AddressNotCorrect))
                    }
                }

                val result = jsonRpc.getString()
                if (result != null) {
                    call.respondText(result, ContentType.Application.Json, HttpStatusCode.OK)
                } else {
                    call.respondText("{}", ContentType.Application.Json, HttpStatusCode.BadRequest)
                }
            }
        }
    }.start(wait = true)


}



fun getItem(jsonItem: JsonItem, user: Int): Item {
    return Item(jsonItem.hash, user, jsonItem.method, jsonItem.url , jsonItem.query, jsonItem.auth )
}
