package venkman

import org.apache.http.HttpResponse

data class RequestModel(val url: String, val response: HttpResponse?, val loading: Boolean)
