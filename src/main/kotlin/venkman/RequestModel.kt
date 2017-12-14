package venkman

data class RequestModel(
        val url: String,
        val headers: List<Pair<String, String>>,
        val method: String
)