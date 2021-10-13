package Models.Raw

import Models.Auth


data class JsonItemRaw(
    val hash: String,
    val method: String,
    val url: String,
    val query: String?,
    val auth: Auth?,
)
