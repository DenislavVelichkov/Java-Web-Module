package app.util.interfaces;

import java.util.List;

public interface HTTPParser {
    String parseRequest(List<String> args);
}
