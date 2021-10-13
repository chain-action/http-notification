package Models.Raw

import Enums.Method


data class ItemRaw(
    val uid: String,
    val user: Int,
    val method: Int,
    val url: String,
    val query: String?,
    val auth: String?,
)
