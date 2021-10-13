package Redis

import com.uchuhimo.konf.source.yaml
import redis.clients.jedis.Jedis

class Redis(configRedisYaml: com.uchuhimo.konf.Config?=null): Jedis() {

//    private var configRedisYaml: com.uchuhimo.konf.Config

    private val _configRedisYaml = configRedisYaml ?: com.uchuhimo.konf.Config { addSpec(RedisConf) }.from.yaml.file("./config.yml")

    val jedis: Jedis

    init {

//        configRedisYaml = com.uchuhimo.konf.Config { addSpec(RedisConf) }.from.yaml.file("./config.yml")

        val host = _configRedisYaml[RedisConf.host]
        val port = _configRedisYaml[RedisConf.port]
        val database = _configRedisYaml[RedisConf.database]

        jedis = Jedis(host, port)
        jedis.select(database)
        jedis.connect()
    }

}