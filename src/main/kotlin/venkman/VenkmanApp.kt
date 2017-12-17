package venkman

import org.apache.http.client.methods.*
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import java.io.IOException
import javax.swing.JOptionPane

class VenkmanApp {
    val listeners: MutableList<(ResponseModel) -> Unit> = mutableListOf()
    val mainWindow: MainWindow = MainWindow(this)
    val httpClient: CloseableHttpClient = HttpClientBuilder.create()
            .disableRedirectHandling()
            .build();
    var responseModel: ResponseModel? = null


    fun send(requestModel: RequestModel) {
        try {
            mainWindow.loading = true
            val request = createRequest(requestModel)
            httpClient.execute(request).use { response ->
                val headers = response.allHeaders.map { h -> Pair(h.name, h.value) }
                responseModel = ResponseModel(
                        response.statusLine.statusCode,
                        response.statusLine.reasonPhrase,
                        response.statusLine.protocolVersion.toString(),
                        headers,
                        EntityUtils.toString(response.entity)
                )
                for (listener in listeners) {
                    listener(responseModel!!)
                }
            }
        } catch (e1: Exception) {
            JOptionPane.showConfirmDialog(mainWindow, "An error occured: " + e1.message, "An error occured", JOptionPane.ERROR_MESSAGE or JOptionPane.CLOSED_OPTION)
            e1.printStackTrace(System.err)
        } finally {
            mainWindow.loading = false;
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