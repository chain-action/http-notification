package Utils

class HeaderAuthorization {

    private val regex = Regex(pattern = "^Bearer (.+?)$")//Bearer

    fun get(str: String): String? {
        regex.matchEntire(str)?.let {
//            println(it.groupValues)
            val chain = it.groupValues[1]
            return chain
        }
        return null
    }
}