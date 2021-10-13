package Config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class MyArgs(parser: ArgParser) {

    val verbose by parser.flagging(
        "-v", "--verbose",
        help = "enable verbose mode").default(false)
    val debug by parser.flagging(
        "-d", "--debug",
        help = "enable verbose mode").default(false)
    val createtable by parser.flagging(
        "-c", "--createtable",
        help = "create table").default(false)
/*    val createqueues by parser.flagging(
        "-q", "--createqueues",
        help = "create Queues").default(false)
    val createbinding by parser.flagging(
        "-b", "--createbinding",
        help = "create Binding").default(false)
    val createexchange by parser.flagging(
        "-e", "--createexchange",
        help = "create exchange").default(false)*/

/*    val name by parser.storing(
        "-N", "--name",
        help = "name of the user").default("Aleksey")*/
/*    val iterations by parser.storing(
        "-i", "--iterations",
        help = "number iterations"){ toInt() }.default(0)
    val thread by parser.storing(
        "-t", "--thread",
        help = "thread, num coroutines"){ toInt() }.default(0)*/

/*    val help by parser.adding(
        "-I", help = "params in config.yml")*/
/*    val source by parser.positional(
        "SOURCE",
        help = "source filename")*/

}