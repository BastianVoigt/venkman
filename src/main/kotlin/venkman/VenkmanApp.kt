package venkman

import org.apache.http.client.methods.*
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import javax.swing.SwingUtilities

class VenkmanApp {
    val listeners: MutableList<(ResponseModel) -> Unit> = mutableListOf()
    val httpClient: CloseableHttpClient = HttpClientBuilder.create()
            .disableRedirectHandling()
            .build();
    var responseModel: ResponseModel = ResponseModel()
        set(value) {
            field = value
            fireResponseModelChanged()
        }

    init {
        MainWindow(this)
    }


    fun send(requestModel: RequestModel) {
        SwingUtilities.invokeLater {
            try {
                SwingUtilities.invokeLater { responseModel = responseModel.copy(loading = true) }
                val request = createRequest(requestModel)
                httpClient.execute(request).use { response ->
                    val headers = response.allHeaders.map { h -> Pair(h.name, h.value) }
                    val body = when (response.entity) {
                        null -> ""
                        else -> EntityUtils.toString(response.entity)
                    }
                    SwingUtilities.invokeLater {
                        responseModel = ResponseModel(
                                loading = false,
                                statusCode = response.statusLine.statusCode,
                                statusReasonPhrase = response.statusLine.reasonPhrase,
                                protocolVersion = response.statusLine.protocolVersion.toString(),
                                headers = headers,
                                body = body
                        )
                    }
                }
            } catch (e1: Exception) {
                e1.printStackTrace(System.err)
                SwingUtilities.invokeLater {
                    responseModel = responseModel.copy(loading = false,
                            statusCode = 0,
                            statusReasonPhrase = "",
                            body = "An error occured: " + e1.message,
                            headers = listOf()
                    )
                }
            }
        }
    }

    private fun fireResponseModelChanged() {
        for (listener in listeners) {
            listener(responseModel)
        }
    }

    private fun createRequest(requestModel: RequestModel): HttpUriRequest {
        var request: HttpUriRequest? = null
        when (requestModel.method) {
            "GET" -> request = HttpGet(requestModel.url)
            "POST" -> request = HttpPost(requestModel.url)
            "PUT" -> request = HttpPut(requestModel.url)
            "HEAD" -> request = HttpHead(requestModel.url)
            "DELETE" -> request = HttpDelete(requestModel.url)
            else -> throw IllegalArgumentException("Invalid method " + requestModel.method)
        }
        for (header in requestModel.headers) {
            request.addHeader(header.first, header.second)
        }
        return request
    }

    fun addListener(listener: (ResponseModel) -> Unit) {
        listeners.add(listener)
    }
}