package venkman;

import org.apache.http.HttpResponse;

public class RequestModel {
    private String url;
    private HttpResponse response;
    private boolean loading;

    public HttpResponse getResponse() {
        return response;
    }

    public RequestModel setResponse(HttpResponse response) {
        this.response = response;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public RequestModel setUrl(String url) {
        this.url = url;
        return this;
    }
}
