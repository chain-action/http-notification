package RobotsTxt

data class RobotsTxtData(
    val UserAgent: String,
    val Allow: List<String>,
    val Disallow: List<String>,
    val sitemap: List<String>,
)
