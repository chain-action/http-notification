package RobotsTxt

import mu.KotlinLogging

class RobotsTxt {

    private val logger = KotlinLogging.logger {}

    private val regexLine = Regex(pattern ="""([a-z\-]+): (.+)""", setOf( RegexOption.IGNORE_CASE ))

    private fun newRobotsTxtData(
        user_agent: String,
        arrayAllow: ArrayList<String>,
        disallow: ArrayList<String>,
        sitemap: ArrayList<String>
    ): RobotsTxtData {
        return RobotsTxtData(
            user_agent, arrayAllow.clone() as List<String>,
            disallow.clone() as List<String>,
            sitemap.clone() as List<String>
        )
    }

    fun parseDirectives(text: String): List<RobotsTxtData> {
        var old_user_agent: String?=null
        var user_agent: String?=null
        val arrayAllow = arrayListOf<String>()
        val disallow= arrayListOf<String>()
        val sitemap= arrayListOf<String>()

        val listRobotsTxtData= arrayListOf<RobotsTxtData>()

        regexLine.findAll(text).iterator().forEach { matchResults->
            val key = matchResults.groupValues[1].trim()
            val pre_value = matchResults.groupValues[2]
            val value = pre_value.split("#").first().trim()
//            logger.debug { "matchResults: key: ${key}, value: ${value}" }
            if (key == """User-agent""") {
                user_agent = value
                if (old_user_agent != user_agent && old_user_agent != null) {
                    val robotsTxtData = newRobotsTxtData(old_user_agent!!, arrayAllow, disallow, sitemap)
                    listRobotsTxtData.add(robotsTxtData)
                    arrayAllow.clear()
                    disallow.clear()
                    sitemap.clear()
                    logger.debug { "robotsTxtData: ${robotsTxtData}" }
                }
                old_user_agent = user_agent
            } else {
                    when (key) {
                        """Allow""" -> arrayAllow.add(value)
                        """Disallow""" -> disallow.add(value)
                        """sitemap""" -> sitemap.add(value)
                    }
            }
        }
        if (user_agent!=null) {
            val robotsTxtData = RobotsTxtData(user_agent!!, arrayAllow, disallow, sitemap)
            listRobotsTxtData.add(robotsTxtData)
        }
        return listRobotsTxtData
    }
}