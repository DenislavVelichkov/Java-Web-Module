package app.models.interfaces;

import java.util.HashMap;

public interface HttpResponse {
    HashMap<String, String> getHeaders();

    int getStatusCode();

    byte[] getContent();

    byte[] getBytes();

    byte[] setBytes(byte[] bytes);

    void setStatusCode(int statusCode);

    void setContent(byte[] content);

    void addHeader(String header, String value);
}
