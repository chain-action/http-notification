package Models

import Enums.Method

data class JsonItem(
    val hash: String,
    val method: Method,
    val url: String,
    val query: String,
    val auth: Auth?,
)
