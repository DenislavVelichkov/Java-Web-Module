package app;

import app.util.HTTPParserImpl;
import app.util.interfaces.HTTPParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        HTTPParser parser = new HTTPParserImpl();

        String[] urls = reader.readLine().split("\\s");
        List<String> request = new ArrayList<>();

        String input = reader.readLine();
        int blankLineCounter = 0;

        while ((input != null && input.length() > 0) ||
                (blankLineCounter != 1)) {
            if (input.isBlank()) {
                blankLineCounter++;
            }

            request.add(input);
            input = reader.readLine();
        }

        if (authenticateRequest(urls, request)) {
            String parsedRequest = parser.parseRequest(request);
            System.out.println(parsedRequest);
        }
    }

    private static boolean authenticateRequest(String[] urls, List<String> request) {
        String[] tokens = request.get(0).split("\\s");
        return Arrays.stream(urls).anyMatch(s -> s.equals(tokens[1]));
    }
}
