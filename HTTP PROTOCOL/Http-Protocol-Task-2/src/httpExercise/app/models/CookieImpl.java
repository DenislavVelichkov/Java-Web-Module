package httpExercise.app.models;

import httpExercise.app.models.interfaces.Cookie;

import java.util.HashMap;
import java.util.Map;

public class CookieImpl implements Cookie {
    private Map<String, String> cookie;

    CookieImpl() {
        this.cookie = new HashMap<>();
    }


    @Override
    public void setCookie(String param1, String param2) {
        this.cookie.putIfAbsent(param1, param2);
    }

    @Override
    public Map<String, String> getCookieContent() {
        return this.cookie;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.cookie.forEach((key, value) -> {
            sb.append(key).append(" <-> ").append(value).append(System.lineSeparator());
        });

        return sb.toString().trim();
    }
}
