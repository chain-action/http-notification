package Config

import com.uchuhimo.konf.ConfigSpec

class HttpServer {
    companion object : ConfigSpec("http_server") {
        val port by optional(8000)
        val host by optional("0.0.0.0")
        val cors_hosts by optional<List<String>?>(null)
//        val requestQueueLimit by optional(16)
//        val responseWriteTimeoutSeconds by optional(10)

    }
}