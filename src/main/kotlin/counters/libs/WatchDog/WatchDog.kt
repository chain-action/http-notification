package WatchDog

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException




class WatchDog(val msTimeOut: Long= 5000, val msDelay: Long= 1000) {

    private var oldCurrentMs: Long = 0L
    private var run = true

    fun run(){
        GlobalScope.launch {
            oldCurrentMs = System.currentTimeMillis()
   /*         repeat((msDelay/1000).toInt()){

            }*/

            while (run) {
                val currentMs = System.currentTimeMillis()
                val raznica = (currentMs - oldCurrentMs)
                if (raznica>msTimeOut) {
                    println("WatchDog Exception System.exit(1)")
                    System.exit(1)
                    throw Exception("WatchDog Exception ")
//                    throw IOException()
                } else if (raznica>msDelay) {
//                    println("WatchDog: not ping $raznica")
                }

//                    oldCurrentMs = currentMs
//                    println("Hello from Watch Dogs Coroutines")
                delay(msDelay)
            }
        }
    }

    fun ping(){
        oldCurrentMs = System.currentTimeMillis()
    }
    fun stop(){
        run = false
    }

}