package app.models.interfaces;

import java.util.HashMap;

public interface HttpResponse {
    HashMap<String, String> getHeaders();

    int getStatusCode();

    byte[] getContent();

    byte[] getBytes();

    void setBytes(byte[] bytes);

    void setStatusCode(int statusCode);

    void setContent(byte[] content);

    void addHeader(String header, String value);

    void setHeader(HashMap<String, String> header);
}
