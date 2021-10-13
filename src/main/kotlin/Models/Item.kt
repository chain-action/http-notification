package Models

import Enums.Method

data class Item(
   val uid: String,
   val user: Int,
   val method: Method,
   val url: String,
   val query: String,
   val auth: Auth?,
)

