package Utils

import kotlin.math.pow

class UtilMathHttpRetry(
    private val coeff_retry: Double
) {

    fun calc(retry: Int): Double {

        return coeff_retry.pow(retry)
    }
}