package Models

data class ParameterStructure(
    val name: String,
    val type: String,
    val descr: String,
    val required: Boolean=true,
    val default: String?=null,
)
