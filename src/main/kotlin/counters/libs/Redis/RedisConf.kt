package Redis

import com.uchuhimo.konf.ConfigSpec

class RedisConf {
    companion object : ConfigSpec("redis") {
        val host by optional("localhost")
        val port by optional(6379)
        val database by optional(0)
    }
}