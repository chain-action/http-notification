package HttpSender

import Enums.ItemDefault
import Enums.Method
import com.uchuhimo.konf.source.yaml
import mu.KotlinLogging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class OkHttpRequest {

    private var user_agent: String
    private var config: com.uchuhimo.konf.Config
    private val client: OkHttpClient

    private val logger = KotlinLogging.logger {}

    private val HeaderAccept = "text/html,application/xhtml+xml,application/json,text/plain,*/*"

    init {
        config = com.uchuhimo.konf.Config { addSpec(HttpSenderConf) }.from.yaml.file("./config.yml")
        val timeout = config[HttpSenderConf.timeout]
        val connect_timeout = config[HttpSenderConf.connect_timeout]
        user_agent = config[HttpSenderConf.user_agent]
        val builder = OkHttpClient.Builder()
//        if (BuildConfig.DEBUG) { builder.addInterceptor(OkHttpProfilerInterceptor()) }

        client = builder
            .connectTimeout(connect_timeout, TimeUnit.SECONDS)
            .readTimeout(timeout.toLong(), TimeUnit.SECONDS)
            .build()
    }

    fun get(url: String, method: Method, query: String, payload: String, headers: Map<String, String>?, basicAuth: Pair<String, String>?): Response? {
        requestBuilder(url, method, query, payload, headers, basicAuth)?.let { request ->
            try {
                logger.debug { "get(): ${request}" }
                client.newCall(request).execute().use { response ->

                    logger.debug { "get() response.code: ${response.code}" }
                    if (!response.isSuccessful) {
                        logger.error { "Unexpected code $response" }
                    }
                    if (response.body != null) {
                        val body = response.body!!.string()
                        logger.info { "get() body: ${body}" }
                        return response
                    }
                }
            } catch (e: Exception) {
                logger.error { "Error get(): ${request} Exception: ${e}" }
                logger.error { "Error : Exception: ${e}" }
            }
        }
        return null
    }

    private fun requestBuilder(url: String, method: Method, query: String?, payload: String, headers: Map<String, String>?, basicAuth: Pair<String, String>?): Request? {
        val httpUrl = url.toHttpUrlOrNull()
        if (httpUrl==null) return null

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", user_agent)
            .addHeader("Accept", HeaderAccept)
//            .addHeader("Content-Type", "application/json")
//            .addHeader("DNT", "1")
            .addHeader("Accept-Encoding", "gzip, deflate, br")
        if (headers!=null) {
            headers.forEach { key, value ->
                request.addHeader(key, value)
            }
        }

        if (basicAuth!=null) {
            val credential = Credentials.basic(basicAuth.first, basicAuth.second)
            request.addHeader("Authorization", credential)
        }

        if (method == Method.POST) {
            if (query != null) {
                val formBody = FormBody.Builder().add(query, payload)
                request.post(formBody.build())
            }
            else {
                request.post(payload.toRequestBody(MEDIA_TYPE_DATA))
            }
        } else if (method == Method.GET) {
            val httpBuilder = httpUrl.newBuilder()
            val httpQuery = if (query!=null) httpBuilder.addQueryParameter(query, payload)
            else httpBuilder.addQueryParameter(ItemDefault.QUERY.value, payload)
            request.url(httpQuery.build()).get()
        } else if (method == Method.PUT) {
            if (query != null) {
                val formBody = FormBody.Builder().add(query, payload)
                request.put(formBody.build())
            }
            else {
                request.put(payload.toRequestBody(MEDIA_TYPE_DATA))
            }

        } else if (method == Method.JSONRPC) {

            if (query!=null) {
                val json = JSONObject()
                json.append(query, payload)
                request.post(json.toString().toRequestBody(MEDIA_TYPE_JSON))
            }
            else {
                val json = "{$payload}"
                request.post(json.toRequestBody(MEDIA_TYPE_JSON))
            }
        }
//        if (query!=null)   request.post(query.toRequestBody(MEDIA_TYPE_JSON))
        return request.build()
    }

    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
        val MEDIA_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded".toMediaType()
        val MEDIA_TYPE_DATA = "multipart/form-data".toMediaType()
    }
}