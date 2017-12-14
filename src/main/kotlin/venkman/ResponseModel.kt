package venkman

data class ResponseModel(
        val statusCode: Int,
        val statusReasonPhrase: String,
        val protocolVersion: String,
        val headers: List<Pair<String, String>>,
        val body: String
)