package app.util;

import app.models.HttpRequestImpl;
import app.models.HttpResponseImpl;
import app.models.interfaces.HttpRequest;
import app.models.interfaces.HttpResponse;
import app.util.interfaces.Base64Parser;
import app.util.interfaces.HttpParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpParserImpl implements HttpParser {
    private final static String CORRECT_RESPONSE_BODY =
            "Greetings %s! You have successfully created %s with %s – %s, %s – %s.";
    private final static String NOT_FOUND_REQUEST_BODY =
            "The requested functionality was not found";
    private final static String BAD_REQUEST_BODY =
            "There was an error with the requested functionality due to malformed request.";
    private final static String UNAUTHORIZED_REQUEST_BODY =
            "You are not authorized to access the requested functionality.";
    private final static String USER_NAME = "Pesho";
    private List<String> input;
    private String[] urls;
    private Base64Parser base64Parser;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    public HttpParserImpl(List<String> input, String[] urls) {
        this.input = input;
        this.urls = urls;
        this.base64Parser = new Base64ParserImpl();
        this.httpRequest = new HttpRequestImpl();
        this.httpResponse = new HttpResponseImpl();
    }

    @Override
    public void parseRequest() {
        String[] tokens = this.input.get(0).split("\\s");
        this.httpRequest.setMethod(tokens[0]);
        this.httpRequest.setRequestUrl(tokens[1]);

        this.input
                .stream()
                .skip(1)
                .filter(s -> !s.trim().isEmpty() && !s.contains("&") && !s.contains("Authorization:"))
                .forEach(arg -> {
                    String[] params = arg.split("\\s");

                    this.httpRequest.addHeader(params[0], params[1]);
                });

        this.input.stream().filter(s -> s.contains("&")).forEach(arg -> {
            String[] bodyParams = arg.split("&");
            Arrays.stream(bodyParams).forEach(param -> {
                String[] element = param.split("=");

                this.httpRequest.addBodyParameter(element[0], element[1]);
            });
        });


    }

    @Override
    public void createResponse() {
        if (argsContainsAuthorization(input) &&
                authorizeAccess(this.input)) {
            StringBuilder sb = new StringBuilder();

            this.httpResponse.setStatusCode(200);
            this.httpResponse.setBytes("OK".getBytes());
            for (Map.Entry<String, String> entry : this.httpRequest.getHeaders().entrySet()) {
                this.httpResponse.addHeader(entry.getKey(), entry.getValue());
            }

            byte[] result = buildContent(this.httpRequest.getBodyParameters());
            this.httpResponse.setContent(result);
        } else if (!this.argsContainsAuthorization(this.input)) {
            this.httpResponse.setStatusCode(401);
        } else if (authenticateRequest(urls, input)) {
            this.httpResponse.setStatusCode(404);
        } else if (isBodyPresent(this.input)) {
            this.httpResponse.setStatusCode(400);
        }
    }

    public byte[] buildContent(HashMap<String, String> bodyParameters) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(" ");
        }
        String[] tokens = sb.toString().split("\\s");


        return String.format(CORRECT_RESPONSE_BODY,
                USER_NAME, tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]).getBytes();
    }

    @Override
    public String sendResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ")
                .append(this.httpResponse.getStatusCode())
                .append(" ")
                .append(new String(this.httpResponse.getBytes()))
        .append(System.lineSeparator());
        this.httpResponse.getHeaders()
                .forEach((key, value) ->
                        sb.append(key)
                                .append(" ")
                                .append(value)
                                .append(System.lineSeparator()));
        sb.append(System.lineSeparator())
        .append(new String(this.httpResponse.getContent()));

        return sb.toString().trim();
    }

    @Override
    public boolean argsContainsAuthorization(List<String> args) {
        return args.stream().anyMatch(s -> s.contains("Authorization:"));
    }

    @Override
    public boolean authorizeAccess(List<String> args) {
        String[] encodedUsername =
                this.input
                        .stream()
                        .filter(s -> s.contains("Authorization:"))
                        .findAny()
                        .get()
                        .split("\\s");

        String authority = base64Parser.decodeString(encodedUsername[2]);

        return authority.equals(USER_NAME);
    }

    @Override
    public boolean authenticateRequest(String[] urls, List<String> request) {
        String[] tokens = request.get(0).split("\\s");
        return Arrays.stream(urls).anyMatch(s -> s.equals(tokens[1]));
    }

    private boolean isBodyPresent(List<String> input) {
        return input.stream().anyMatch(s -> s.contains("&"));
    }
}