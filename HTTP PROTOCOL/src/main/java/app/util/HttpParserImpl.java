package app.util;

import app.common.ResponseMessages;
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

        if (!isDatePresent()) {
            this.httpRequest.addHeader("Date:", ResponseMessages.DEFAULT_DATE);
        }

        this.input
                .stream()
                .skip(1)
                .filter(s -> !s.trim().isEmpty() &&
                        !s.contains("&") &&
                        !s.contains("Authorization:"))
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
        if (this.argsContainsAuthorization() &&
                this.isUrlPresent(urls) &&
                this.isBodyPresent()) {
            this.httpResponse.setStatusCode(200);
            this.httpResponse.setBytes(ResponseMessages.OK.getBytes());
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            byte[] bodyContent = buildBodyContent(this.httpRequest.getBodyParameters());
            this.httpResponse.setContent(bodyContent);
        } else if (!this.argsContainsAuthorization()) {
            this.httpResponse.setStatusCode(401);
            this.httpResponse.setBytes(ResponseMessages.UNAUTHORIZED_ACCESS.getBytes());
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            this.httpResponse.setContent(
                    ResponseMessages.UNAUTHORIZED_REQUEST_BODY.getBytes());
        } else if (!this.isUrlPresent(urls)) {
            this.httpResponse.setStatusCode(404);
            this.httpResponse.setBytes(ResponseMessages.NOT_FOUND.getBytes());
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            this.httpResponse.setContent(ResponseMessages.NOT_FOUND_REQUEST_BODY.getBytes());
        } else if (!this.isBodyPresent()) {
            this.httpResponse.setStatusCode(400);
            this.httpResponse.setBytes(ResponseMessages.BAD_REQUEST.getBytes());
            this.httpResponse.setHeader(this.httpRequest.getHeaders());
            this.httpResponse.setContent(
                    ResponseMessages.BAD_REQUEST_BODY.getBytes());
        }
    }

    public byte[] buildBodyContent(HashMap<String, String> bodyParameters) {
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
    public boolean argsContainsAuthorization() {
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

    @Override
    public boolean isUrlPresent(String[] urls) {
        String[] tokens = this.input.get(0).split("\\s");
        return Arrays.stream(urls).anyMatch(s -> s.equals(tokens[1]));
    }

    @Override
    public boolean isBodyPresent() {
        return input.stream().anyMatch(s -> s.contains("&"));
    }

    @Override
    public boolean isDatePresent() {
        return this.input.stream().anyMatch(s -> s.contains("Date:"));
    }
}