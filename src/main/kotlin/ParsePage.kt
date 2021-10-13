import Config.App
import Models.Auth
import ch.qos.logback.classic.spi.CallerData.extract
import com.uchuhimo.konf.source.yaml
import it.skrape.core.htmlDocument
import it.skrape.matchers.toBe
import it.skrape.matchers.toContain
import it.skrape.selects.attribute
import it.skrape.selects.html5.h1
import it.skrape.selects.html5.head
import it.skrape.selects.html5.meta
import it.skrape.selects.html5.p
import mu.KotlinLogging
import java.io.IOException


class ParsePage(/*config: com.uchuhimo.konf.Config?=null*/) {

    private val logger = KotlinLogging.logger {}

//    private val configApp = config ?: com.uchuhimo.konf.Config { addSpec(App) }.from.yaml.file("./config.yml")

    fun getMetaContents(textHtml: String, name: String): List<String> {
        val array = arrayListOf<String>()
        try {
            htmlDocument(textHtml) {
                meta {
                    withAttribute = "name" to name

                    findAll {
                        attribute("content")
                    }?.let {
                        array.addAll(it.split(", "))
//                        logger.debug { it }
                    }

                }
//                logger.debug { array }
            }
        } catch (e: Exception) {
            logger.error { "Exception $e" }
        }
        return array
    }

}