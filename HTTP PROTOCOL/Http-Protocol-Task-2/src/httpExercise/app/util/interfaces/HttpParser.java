package httpExercise.app.util.interfaces;

public interface HttpParser {
    void parseRequest();

    void createResponse();

    String sendResponse();

    String printCookie();
}
