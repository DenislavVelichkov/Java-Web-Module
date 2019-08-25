package httpExercise.app.util;

import httpExercise.app.common.ResponseMessages;
import httpExercise.app.models.HttpRequestImpl;
import httpExercise.app.models.HttpResponseImpl;
import httpExercise.app.models.interfaces.HttpRequest;
import httpExercise.app.models.interfaces.HttpResponse;
import httpExercise.app.util.interfaces.Base64Parser;
import httpExercise.app.util.interfaces.HttpParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpParserImpl implements HttpParser {
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
                .filter(s -> !s.trim().isEmpty() &&
                        !s.contains("&") &&
                        !s.contains("Authorization:"))
                .forEach(arg -> {
                    String[] params = arg.split("\\s");
                    if (params[0].equals("Date:") ||
                            params[0].equals("Host:") ||
                            params[0].equals("Content-Type:")) {
                        this.httpRequest.addHeader(params[0], params[1]);
                    }
                });

        this.input.stream().filter(s -> s.contains("&")).forEach(arg -> {
            String[] bodyParams = arg.split("&");
            Arrays.stream(bodyParams).forEach(param -> {
                String[] element = param.split("=");

                this.httpRequest.addBodyParameter(element[0], element[1]);
            });
        });

        if (this.containsCookie(input)) {
            String[] tokenParams =
                    this.input.stream()
                            .filter(s -> s.contains("Cookie:"))
                            .findFirst()
                            .get()
                            .split("\\s");

            Arrays.stream(tokenParams).skip(1).forEach(arg -> {
                String[] input = arg.split("=");

                this.httpRequest.getCookie().setCookie(input[0].replace(";",""), input[1].replace(";",""));
            });
        }
    }

    private boolean containsCookie(List<String> input) {
        return input.stream().anyMatch(s -> s.contains("Cookie:"));
    }

    @Override
    public void createResponse() {
        if (this.isConnectionAuthorized() &&
                this.isUrlPresent(urls) &&
                this.isBodyPresent()) {

            this.httpResponse.setStatusCode(200);
            this.httpResponse.setStatusString(ResponseMessages.OK);
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            byte[] bodyContent = this.buildBodyContent(this.httpRequest.getBodyParameters());
            this.httpResponse.setContent(bodyContent);

        } else if (!this.isConnectionAuthorized()) {

            this.httpResponse.setStatusCode(401);
            this.httpResponse.setStatusString(ResponseMessages.UNAUTHORIZED_ACCESS);
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            this.httpResponse.setContent(
                    ResponseMessages.UNAUTHORIZED_REQUEST_BODY.getBytes());

        } else if (!this.isUrlPresent(urls)) {

            this.httpResponse.setStatusCode(404);
            this.httpResponse.setStatusString(ResponseMessages.NOT_FOUND);
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            this.httpResponse.setContent(ResponseMessages.NOT_FOUND_REQUEST_BODY.getBytes());

        } else if (!this.isBodyPresent()) {

            this.httpResponse.setStatusCode(400);
            this.httpResponse.setStatusString((ResponseMessages.BAD_REQUEST));
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            this.httpResponse.setContent(
                    ResponseMessages.BAD_REQUEST_BODY.getBytes());

        }
    }

    private byte[] buildBodyContent(HashMap<String, String> bodyParameters) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(" ");
        }
        String[] tokens = sb.toString().split("\\s");


        return String.format(ResponseMessages.CORRECT_RESPONSE_BODY,
                ResponseMessages.USER_NAME, tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]).getBytes();
    }

    @Override
    public String sendResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ")
                .append(this.httpResponse.getStatusCode())
                .append(" ")
                .append(this.httpResponse.getStatusString())
                .append(System.lineSeparator());
        this.httpResponse.getHeaders()
                .forEach((key, value) ->
                        sb.append(key)
                                .append(" ")
                                .append(value)
                                .append(System.lineSeparator()));
        sb.append(System.lineSeparator())
                .append(new String(this.httpResponse.getContent()));
        this.httpResponse.setBytes(sb.toString().trim().getBytes());


        return new String(this.httpResponse.getBytes());
    }

    private boolean isConnectionAuthorized() {
        String username = "";
        String encodedUsername =
                this.input.stream().filter(s -> s.contains("Authorization:"))
                        .findAny()
                        .orElse(null);
        if (encodedUsername != null) {
            String[] tokens = encodedUsername.split("\\s");
            encodedUsername = tokens[2];
        }

        username = this.base64Parser.decodeString(encodedUsername);

        return this.input.stream()
                .anyMatch(s -> s.contains("Authorization:")) && username.equals(ResponseMessages.USER_NAME);
    }


    private boolean isUrlPresent(String[] urls) {
        String[] tokens = this.input.get(0).split("\\s");
        return Arrays.stream(urls).anyMatch(s -> s.equals(tokens[1]));
    }

    private boolean isBodyPresent() {
        return input.stream().anyMatch(s -> s.contains("&"));
    }

    private boolean isDatePresent() {
        return this.input.stream().anyMatch(s -> s.contains("Date:"));
    }

    @Override
    public String printCookie() {
        return this.httpRequest.getCookie().toString();
    }
}