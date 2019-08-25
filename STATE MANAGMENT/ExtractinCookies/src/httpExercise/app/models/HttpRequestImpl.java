package httpExercise.app.models;

import httpExercise.app.models.interfaces.Cookie;
import httpExercise.app.models.interfaces.HttpRequest;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class HttpRequestImpl implements HttpRequest {
    private String requestURL;
    private String method;
    private HashMap<String, String> headers;
    private HashMap<String, String> bodyParameters;
    private Cookie cookie;

    public HttpRequestImpl() {
        this.headers = new LinkedHashMap<>();
        this.bodyParameters = new LinkedHashMap<>();
        this.cookie = new CookieImpl();
    }

    @Override
    public String getRequestUrl() {
        return this.requestURL;
    }

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestURL = requestUrl;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public HashMap<String, String> getBodyParameters() {
        return this.bodyParameters;
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.put(header, value);
    }

    @Override
    public void addBodyParameter(String parameter, String value) {
        this.bodyParameters.put(parameter, value);
    }

    @Override
    public Cookie getCookie() {
        return this.cookie;
    }

    @Override
    public boolean isResource() {
        return false;
    }
}
