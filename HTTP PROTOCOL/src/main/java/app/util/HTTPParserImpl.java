package app.util;

import app.util.interfaces.Base64Parser;
import app.util.interfaces.HTTPParser;

import java.util.List;

public class HTTPParserImpl implements HTTPParser {
    private final static String REQUEST_BODY =
            "Greetings %s! You have successfully created %s with %s – %d, %s – %d.";
    private final static String AUTHORITY_NAME = "Pesho";
    private Base64Parser base64Parser;

    public HTTPParserImpl() {
        this.base64Parser = new Base64ParserImpl();
    }

    public String parseRequest(List<String> args) {
        boolean authorizeAccess = checkIfUserIsLoggedIn(args.get(4));
        StringBuilder sb = new StringBuilder();

        String product = "";
        String param1Name = "";
        int param1Value = 0;
        String param2Name = "";
        int param2value = 0;

        if (argsContainsParams(args) && authorizeAccess) {

            sb.append("HTTP/1.1 200 OK").append(System.lineSeparator())
                    .append(args.stream()
                            .filter(s -> s.contains("Date"))
                            .findFirst()
                            .get()).append(System.lineSeparator())
                    .append(args.stream()
                            .filter(s -> s.contains("Host"))
                            .findFirst()
                            .get()).append(System.lineSeparator())
                    .append(args.stream()
                            .filter(s -> s.contains("Content-Type"))
                            .findFirst()
                            .get()).append(System.lineSeparator())
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

            sb.append(String.format(REQUEST_BODY,
                    AUTHORITY_NAME, product, param1Name, param1Value, param2Name, param2value));
        }


        return sb.toString().trim();
    }

    private boolean checkIfUserIsLoggedIn(String s) {
        String[] tokens = s.split("\\s");
        String authority = base64Parser.decodeString(tokens[2]);

        return authority.equals(AUTHORITY_NAME);
    }

    private boolean argsContainsParams(List<String> args) {
        boolean datePresent = args.stream().anyMatch(s -> s.contains("Date:"));
        boolean hostPresent = args.stream().anyMatch(s -> s.contains("Host:"));
        boolean contentTypePresent = args.stream().anyMatch(s -> s.contains("Content-Type:"));

        return datePresent && hostPresent && contentTypePresent;
    }
}
