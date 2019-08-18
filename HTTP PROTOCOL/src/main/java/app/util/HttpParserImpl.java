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
    private final static String CORRECT_REQUEST_BODY =
            "Greetings %s! You have successfully created %s with %s – %d, %s – %d.";
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
    public void createRequest(List<String> args) {
        String[] tokens = args.get(0).split("\\s");
        this.httpRequest.setMethod(tokens[0]);
        this.httpRequest.setRequestUrl(tokens[1]);

        args.forEach(arg -> {
            String[] params = arg.split("\\s");

            this.httpRequest.addHeader(params[0], params[1]);
        });

        args.forEach(arg -> {
            String[] bodyParams = arg.split("\\s");

            this.httpRequest.addBodyParameter(bodyParams[0], bodyParams[1]);
        });

        /*if (argsContainsParams(args) && authorizeAccess) {

            sb.append("HTTP/1.1 200 OK").append(System.lineSeparator())
                    .append(args.stream()
                            .filter(s -> s.contains("Date"))
                            .findFirst()
                            .orElse(null)).append(System.lineSeparator())
                    .append(args.stream()
                            .filter(s -> s.contains("Host"))
                            .findFirst()
                            .orElse(null)).append(System.lineSeparator())
                    .append(args.stream()
                            .filter(s -> s.contains("Content-Type"))
                            .findFirst()
                            .orElse(null)).append(System.lineSeparator())
                    .append(System.lineSeparator());

            String[] body = args.get(args.size() - 1).split("&");
            for (String s : body) {
                String[] params = s.split("=");
                switch (params[0]) {
                    case "name":
                        product = params[1];
                        break;
                    case "quantity":
                        param1Name = params[0];
                        param1Value = Integer.parseInt(params[1]);
                        break;
                    case "price":
                        param2Name = params[0];
                        param2value = Integer.parseInt(params[1].replace("/t", "").trim());
                }
            }

            sb.append(String.format(CORRECT_REQUEST_BODY,
                    AUTHORITY_NAME, product, param1Name, param1Value, param2Name, param2value));
        }

        if (!argsContainsParams(args)) {

        }*/
        this.createResponse();
    }

    @Override
    public boolean authenticateRequest(String[] urls, List<String> request) {
        String[] tokens = request.get(0).split("\\s");
        return Arrays.stream(urls).anyMatch(s -> s.equals(tokens[1]));
    }

    @Override
    public void createResponse() {
        if (argsContainsParams(input)) {
            StringBuilder sb = new StringBuilder();

            this.httpResponse.setStatusCode(200);
            this.httpResponse.setBytes("OK".getBytes());
            for (Map.Entry<String, String> entry : this.httpRequest.getHeaders().entrySet()) {
                this.httpResponse.addHeader(entry.getKey(), entry.getValue());
            }

            byte[] result = buildContent(this.httpRequest.getBodyParameters());
            this.httpResponse.setContent(result);

        } else if (!authorizeAccess(this.httpRequest.getBodyParameters().get("name"))) {
            this.httpResponse.setStatusCode(401);
        } else if (authenticateRequest(urls, input)) {

        }
    }

    public byte[] buildContent(HashMap<String, String> bodyParameters) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
            if (entry.getKey().equals("name")) {
                sb.append("")
            }
        }
    }

    @Override
    public String sendResponse() {
        StringBuilder sb = new StringBuilder();

        sb.append("")

        return null;
    }

    @Override
    public boolean argsContainsParams(List<String> args) {
        boolean datePresent = args.stream().anyMatch(s -> s.contains("Date:"));
        boolean hostPresent = args.stream().anyMatch(s -> s.contains("Host:"));
        boolean contentTypePresent = args.stream().anyMatch(s -> s.contains("Content-Type:"));

        return datePresent && hostPresent && contentTypePresent;
    }

    @Override
    public boolean authorizeAccess(String parameter) {
        String authority = base64Parser.decodeString(parameter);

        return authority.equals(USER_NAME);
    }

}