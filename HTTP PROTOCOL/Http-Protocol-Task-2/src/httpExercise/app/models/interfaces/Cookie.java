package httpExercise.app.models.interfaces;

import java.util.Map;

public interface Cookie {

    void setCookie(String param1, String param2);

    Map<String, String> getCookieContent();
}
