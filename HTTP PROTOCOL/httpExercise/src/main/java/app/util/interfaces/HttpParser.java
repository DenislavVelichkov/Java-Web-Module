package app.util.interfaces;

import java.util.HashMap;

public interface HttpParser {
    void parseRequest();

    void createResponse();

    String sendResponse();

    boolean isConnectionAuthorized();

    boolean isUrlPresent(String[] urls);

    byte[] buildBodyContent(HashMap<String, String> bodyParameters);

    boolean isBodyPresent();

    boolean isDatePresent();
}
