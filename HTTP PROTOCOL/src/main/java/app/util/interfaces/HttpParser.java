package app.util.interfaces;

import java.util.HashMap;
import java.util.List;

public interface HttpParser {
    void parseRequest();

    void createResponse();

    String sendResponse();

    boolean authorizeAccess(List<String> args);

    boolean argsContainsAuthorization(List<String> args);

    boolean authenticateRequest(String[] urls, List<String> request);

    byte[] buildContent(HashMap<String, String> bodyParameters);
}
