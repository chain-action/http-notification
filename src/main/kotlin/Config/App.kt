package Config

import com.uchuhimo.konf.ConfigSpec

class App {
    companion object : ConfigSpec("app") {
        val kafka_topic by optional<String>("kafka_topic")
        val coeff_retry by required<Double>() // 1.9
        val max_repeat by optional<Int>(15)
        val max_delay by optional<Long>(60*60*12*1000) // 12 hours
        val http_accept_domain_debug by optional<List<String>?>(null)
        val http_accept_user_agent by optional<List<String>?>(null)
        val meta_verification_name by optional<List<String>?>(null)
        val info_title by required<String>()
        val info_descr by required<String>()
        val info_logo by required<String>()
        val info_support_surf by required<String>()
    }
}

