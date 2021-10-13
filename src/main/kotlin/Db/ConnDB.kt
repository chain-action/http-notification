package Db


import com.uchuhimo.konf.source.yaml
import org.jetbrains.exposed.sql.Database
import java.io.IOException

class ConnDB{

//    val logger: Logger = LoggerFactory.getLogger(ConnDB::class.java)

    private val configMysqlYaml = com.uchuhimo.konf.Config { addSpec(MysqlConf) }.from.yaml.file("./config.yml")


    fun getDatabase(): Database? {
        try {
            return  connect(
                configMysqlYaml[MysqlConf.host],
                configMysqlYaml[MysqlConf.port],
                configMysqlYaml[MysqlConf.database],
                configMysqlYaml[MysqlConf.user],
                configMysqlYaml[MysqlConf.password],
                configMysqlYaml[MysqlConf.unicode],
                configMysqlYaml[MysqlConf.character]
            )
        } catch (e: IOException) {
            println("Exception $e")
            return null
        }
    }

    private fun connect(
        host: String,
        port: Int,
        database: String,
        user: String,
        password: String,
        unicode: String = "true",
        character: String = "UTF-8"  //utf8mb4
    ): Database {
        return Database.connect(
//    "jdbc:mysql://127.0.0.1:4306/test?useUnicode=true&characterEncoding=UTF-8&character_set_server=$character",
            "jdbc:mysql://$host:$port/$database?useUnicode=$unicode&characterEncoding=UTF-8&character_set_server=$character&",
            driver = "com.mysql.cj.jdbc.Driver",
            user = user,
            password = password
        )
    }
}