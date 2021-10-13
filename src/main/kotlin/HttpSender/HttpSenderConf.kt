package HttpSender

import com.uchuhimo.konf.ConfigSpec

class HttpSenderConf {
    companion object : ConfigSpec("http_sender") {
        val timeout by optional(60.0)
        val connect_timeout by optional<Long>(20)
        val user_agent by optional("OkHttp.java")

    }
}