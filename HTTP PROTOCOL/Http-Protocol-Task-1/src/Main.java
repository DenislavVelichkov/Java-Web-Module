import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    class ResponseMessages {
        public final static String DEFAULT_DATE = "17/01/2019";
        public final static String CORRECT_RESPONSE_BODY =
                "Greetings %s! You have successfully created %s with %s - %s, %s - %s.";
        public final static String NOT_FOUND_REQUEST_BODY =
                "The requested functionality was not found.";
        public final static String BAD_REQUEST_BODY =
                "There was an error with the requested functionality due to malformed request.";
        public final static String UNAUTHORIZED_REQUEST_BODY =
                "You are not authorized to access the requested functionality.";
        public final static String USER_NAME = "Pesho";
        public final static String UNAUTHORIZED_ACCESS = "HTTP/1.1 401 Unauthorized";
        public final static String NOT_FOUND = "HTTP/1.1 404 Not Found";
        public final static String BAD_REQUEST = "HTTP/1.1 400 Bad Request";
        public final static String OK = "HTTP/1.1 200 OK";
    }

    public static String requestURL;
    public static String method;
    public static HashMap<String, String> headers = new LinkedHashMap<>();
    public static HashMap<String, String> bodyParameters = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        String[] urls = reader.readLine().split("\\s");
        List<String> request = new ArrayList<>();


        String input = "";
        while ((input = reader.readLine()) != null && input.length() > 0) {
            request.add(input);
        }
        request.add(System.lineSeparator());
        while ((input = reader.readLine()) != null && input.length() > 0) {
            request.add(input);
        }


        parseRequest(request);
        System.out.println(createResponse(urls, request));
    }

    private static String createResponse(String[] urls, List<String> request) {
        StringBuilder sb = new StringBuilder();

        if (isConnectionAuthorized(request) &&
                isUrlPresent(urls, request) &&
                isBodyPresent(request)) {

            sb.append(ResponseMessages.OK).append(System.lineSeparator());
            headers.forEach((key, value) -> sb.append(key).append(" ").append(value).append(System.lineSeparator()));
            sb.append(System.lineSeparator());
            sb.append(buildBodyContent());

        }
        if (!isUrlPresent(urls, request)) {

            sb.append(ResponseMessages.NOT_FOUND).append(System.lineSeparator());
            headers.forEach((key, value) -> sb.append(key).append(" ").append(value).append(System.lineSeparator()));
            sb.append(System.lineSeparator());
            sb.append(ResponseMessages.NOT_FOUND_REQUEST_BODY);

        }
        if (!isConnectionAuthorized(request)) {

            sb.append(ResponseMessages.UNAUTHORIZED_ACCESS).append(System.lineSeparator());
            headers.forEach((key, value) -> sb.append(key).append(" ").append(value).append(System.lineSeparator()));
            sb.append(System.lineSeparator());
            sb.append(ResponseMessages.UNAUTHORIZED_REQUEST_BODY);

        }
        if (!isBodyPresent(request)) {

            sb.append(ResponseMessages.BAD_REQUEST).append(System.lineSeparator());
            headers.forEach((key, value) -> sb.append(key).append(" ").append(value).append(System.lineSeparator()));
            sb.append(System.lineSeparator());
            sb.append(ResponseMessages.BAD_REQUEST_BODY);

        }

        return sb.toString().trim();
    }

    private static boolean isBodyPresent(List<String> input) {
        return input.stream().anyMatch(s -> s.contains("&"));
    }

    private static boolean isUrlPresent(String[] urls, List<String> input) {
        String[] tokens = input.get(0).split("\\s");
        return Arrays.stream(urls).anyMatch(s -> s.equals(tokens[1]));
    }

    private static boolean isConnectionAuthorized(List<String> input) {
        String username = "";
        String encodedUsername =
                input.stream().filter(s -> s.contains("Authorization:"))
                        .findAny()
                        .orElse(null);

        if (encodedUsername != null) {
            String[] tokens = encodedUsername.split("\\s");
            encodedUsername = tokens[2];
            Base64.Decoder decoder = Base64.getDecoder();
            username = new String(decoder.decode(encodedUsername));
        }


        return input.stream()
                .anyMatch(s -> s.contains("Authorization:")) && username.equals(ResponseMessages.USER_NAME);
    }

    private static String buildBodyContent() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(" ");
        }
        String[] tokens = sb.toString().split("\\s");


        return String.format(ResponseMessages.CORRECT_RESPONSE_BODY,
                ResponseMessages.USER_NAME, tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
    }

    private static void parseRequest(List<String> input) {
        String[] tokens = input.get(0).split("\\s");
        method = tokens[0];
        requestURL = tokens[1];

        input
                .stream()
                .skip(1)
                .filter(s -> !s.trim().isEmpty() &&
                        !s.contains("&") &&
                        !s.contains("Authorization:"))
                .forEach(arg -> {
                    String[] params = arg.split("\\s");
                    if (params[0].equals("Date:") ||
                            params[0].equals("Host:") ||
                            params[0].equals("Content-Type:")) {
                        headers.put(params[0], params[1]);
                    }
                });

        input.stream().filter(s -> s.contains("&")).forEach(arg -> {
            String[] bodyParams = arg.split("&");

            Arrays.stream(bodyParams).forEach(param -> {
                String[] element = param.split("=");
                bodyParameters.put(element[0], element[1]);
            });
        });
    }

    private static boolean isDatePresent(List<String> input) {
        return input.stream().anyMatch(s -> s.contains("Date:"));
    }
}
