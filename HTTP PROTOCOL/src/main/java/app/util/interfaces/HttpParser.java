package app.util.interfaces;

import java.util.HashMap;
import java.util.List;

public interface HttpParser {
    void createRequest(List<String> args);

    void createResponse();

    String sendResponse();

    boolean authorizeAccess(String parameter);

    boolean argsContainsParams(List<String> args);

    boolean authenticateRequest(String[] urls, List<String> request);

    byte[] buildContent(HashMap<String, String> bodyParameters);
}
