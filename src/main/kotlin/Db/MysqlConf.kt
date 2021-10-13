package Db

import com.uchuhimo.konf.ConfigSpec

class MysqlConf {
    companion object : ConfigSpec("mysql") {
        val host by optional("127.0.0.1")
//        val type by optional("mysql")
        val user by required<String>()
        val password by required<String>()
        val port by optional(3306)
        val database by required<String>()
        val character by optional("UTF-8")
        val unicode by optional("true")
    }
}