import Utils.UtilMathHttpRetry
import mu.KotlinLogging
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*


class RetryDB(coeff_retry: Double) {

    private var encoder: Base64.Encoder = Base64.getEncoder()
    private val logger = KotlinLogging.logger {}

    private val db = mutableMapOf<String, Int>()

    private val algorithm = "SHA-256"

    private val utilMathHttpRetry = UtilMathHttpRetry(coeff_retry)

    fun getHash(key: String, uid: String): String {
        val md = MessageDigest.getInstance(algorithm)
        val concat = key+uid
        val digest = md.digest(concat.toByteArray())
        val hash = encoder.encodeToString(digest)
        return hash
    }

    fun getDelayMillis(hash: String, add: Boolean= false): Long {
        val numRetry = getNumRetry(hash, add)
//        logger.info { "numRetry $numRetry" }
        if (numRetry == 0) {
            return 0L
        } else {
            return (utilMathHttpRetry.calc(numRetry) * 1000.0).toLong()
        }
//        return 0
    }

    private fun getNumRetry(hash: String, add: Boolean= false): Int {
        db.get(hash)?.let {
            if (add) db.set(hash, (it+1) )
            return it
        }?:run {
            if (add)
                db.put(hash, 1)
            else
                db.put(hash, 0)
        }
        return 0
    }

    fun reset(hash: String): Unit {
        db.remove(hash)
//        db.put(hash, 0)
    }

/*    fun getDelayMillis(retry: Int): Long {
        return 1
    }*/
}