package JsonRpc

data class RequestData(
    val jsonrpc: String,
    val method: String,
    val params: String,
    val id: Int,
)