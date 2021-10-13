import mu.KotlinLogging

class OffsetDB(topic: String="") {

    private val mapHashToOffset = mutableMapOf<String, ArrayList<Long>>()

//    private var minOffset: Long? = null
    private val offsets = arrayListOf<Long>()
//    private val offsets = arrayListOf<ArrayList<Long>>()


    private var blockedSave = false

    private val logger = KotlinLogging.logger {}

    fun add(hash: String, offset: Long): Unit {
        mapHashToOffset[hash]?.let {
            val newList = it.clone() as ArrayList<Long>
            newList.add(offset)
            mapHashToOffset.put(hash, newList)
        }?: run {
            val newList = arrayListOf(offset)
            mapHashToOffset.put(hash, newList)
        }
/*       if (mapHashToOffset.containsKey(hash)) {
           mapHashToOffset.put(hash, offset)
       } else {

       }*/
//        setMinOffset(offset)
//        logger.info { "mapHashToOffset = $mapHashToOffset" }
    }

    private fun setMinOffset(offset: Long){
        offsets.add(offset)
//        logger.info { "End offsets = $offsets" }
    }

    fun end(hash: String): Unit {
        mapHashToOffset.get(hash)?.let {
            it.forEach {
                setMinOffset(it)
                mapHashToOffset.remove(hash)
            }
        }

    }


    //    fun min() = minOffset
    @Suppress("ControlFlowWithEmptyBody")
    fun minEnd(): Long? {
        while (blockedSave) { }
        offsets.minOrNull()?.let {
            return it
        }
//        val tmpMinOffset= minOffset
//        minOffset=null
        return null
    }

    fun clearEnd(offset: Long){
        offsets.remove(offset)
    }

}