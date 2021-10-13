package Utils

class UtilEnum {

//    val values: Array<Any>?=null

    private val mapInt = mutableMapOf<Int, String>()
    private val mapString = mutableMapOf<String, String>()
    private val mapName = mutableMapOf<String, Boolean>()


    fun put(key: Int, name: String){
        mapInt.put(key, name)
        put(name)
    }

    fun put(key: String, name: String){
        mapString.put(key, name)
        put(name)
    }

    private fun put(name: String){
        mapName.put(name, true)
    }

    fun getName(key: Int): String? {
        return mapInt[key]
    }

    fun getName(key: String): String? {
        println(mapString)
        return mapString[key]
    }
    fun getNameByName(key: String): String? {
        mapName[key]?.let {
            return key
        }
        return null
    }
}