package Models

data class Token(
    val id: Int,
    val user: Int,
    val raw: String,
    val enabled: Boolean,
    )
