import Config.App
import RobotsTxt.RobotsTxt
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import io.ktor.http.*
import mu.KotlinLogging
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

class RightsToPage(configYaml: Config?=null) {

    private val configApp = configYaml ?: com.uchuhimo.konf.Config { addSpec(App) }.from.yaml.file("./config.yml")

    private val logger = KotlinLogging.logger {}
    private val client = OkHttpClient()

    private val parsePage = ParsePage()
    private val robotsTxt = RobotsTxt()
    private val approveCode = ApproveCode()

    private val http_accept_user_agent = configApp[App.http_accept_user_agent]
    private val http_accept_domain_debug = configApp[App.http_accept_domain_debug]
    private val meta_verification_name = configApp[App.meta_verification_name]

    fun get(url: String): Boolean {
        url(url)?.let {
            val domain = it.host
            val scheme = it.scheme
            val port = it.port
            val urlPort = if (port==80 || port==443) "" else ":$port"
//            logger.info { "domainAccept($domain)" }
            if (domainAccept(domain)) return true
            approveCode.get(url)?.let { approveCodeData ->
                val mainPage = "$scheme://$domain$urlPort/"
//                logger.info { "page($mainPage)" }
                if (page(mainPage, approveCodeData.metaMain)) return true
//                logger.info { "page($url)" }
                if (page(url, approveCodeData.metaUrl)) return true
                val urlRobotTxt = "$scheme://$domain$urlPort/robots.txt"
//                logger.info { "robotsTxt($urlRobotTxt)" }
                if (robotsTxt(urlRobotTxt)) return true
            }
        }
        return false
    }

    private fun url(url: String): HttpUrl? {
        return url.toHttpUrlOrNull()
    }

    private fun domainAccept(domain: String): Boolean {
        http_accept_domain_debug?.forEach {
            val regex = Regex(pattern = "${it}$")
            if ( regex.matches(domain) )  return true
//            if (it == domain) return true
        }
        return false
    }

    private fun page(url: String, accept_content: String): Boolean{

        getHtml(url)?.let { textHtml->
            meta_verification_name?.forEach {
                parsePage.getMetaContents(textHtml, it)?.forEach {
                    if ( it== accept_content ) return true
                }
            }
        }
        return false
    }

    private fun robotsTxt(url: String): Boolean{
        getHtml(url)?.let { it ->
//            logger.debug { "getHtml($url) $it" }
            robotsTxt.parseDirectives(it).let {
//                logger.debug { "robotsTxt.parseDirectives() $it" }
                http_accept_user_agent?.forEach { user_agent ->
                    it.forEach {
                        if ( it.UserAgent==user_agent ){
                            it.Allow.forEach {
                                if (it=="""/""") return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun getHtml(url: String): String? {
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    if (response.code == 200) {
                        response.body?.let {
                            val body = it.string()
//                        logger.debug { "body $body" }
                            return body
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error { "Exception $e" }
        }
        return null
    }
}