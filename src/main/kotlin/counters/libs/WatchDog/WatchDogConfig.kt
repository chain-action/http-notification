package WatchDog

import com.uchuhimo.konf.ConfigSpec


class WatchDogConfig {
    companion object : ConfigSpec("watchdog") {
        val timeout_ms by optional<Long>(600000)
        val delay_ms by optional<Long>(1000)
    }
}