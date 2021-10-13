import Config.App
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.math.BigInteger
import java.security.MessageDigest

class ApproveCode(configYaml: Config?=null) {

    private val configApp = configYaml ?: com.uchuhimo.konf.Config { addSpec(App) }.from.yaml.file("./config.yml")

    private val algorithm = "SHA-256" //MD5, SHA-512,SHA-256
    private val logger = KotlinLogging.logger {}

    private val PrePostTxt: Pair<String, String> = "Web" to "Notify"

    fun get(url: String): ApproveCodeData? {
        url.toHttpUrlOrNull()?.let {
            val urlMain = "${it.scheme}://${it.host}/"
            val md = MessageDigest.getInstance(algorithm)
            val metaMain = toHexString(md.digest(addPrePost(urlMain).toByteArray(Charsets.UTF_8)))
            val metaUrl = toHexString(md.digest(addPrePost(url).toByteArray(Charsets.UTF_8)))
//            logger.debug { "urlMain ${addPrePost(urlMain)} ${metaMain}" }
//            logger.debug { "metaUrl ${addPrePost(url)} ${metaUrl}" }
            return ApproveCodeData(metaMain, metaUrl, configApp[App.http_accept_user_agent]?.first(), configApp[App.meta_verification_name]?.first())
        }
        return null
    }

    private fun addPrePost(url: String): String{
        return PrePostTxt.first+url+PrePostTxt.second
    }

    private fun toHexString(digest: ByteArray): String {
        val bigInt = BigInteger(1, digest)
        return bigInt.toString(16)
    }



}