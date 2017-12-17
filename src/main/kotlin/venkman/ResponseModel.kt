package venkman

data class ResponseModel(
        val loading: Boolean = false,
        val statusCode: Int = 0,
        val statusReasonPhrase: String = "",
        val protocolVersion: String = "",
        val headers: List<Pair<String, String>> = listOf(),
        val body: String = ""
)