package httpExercise.app;

import httpExercise.app.util.HttpParserImpl;
import httpExercise.app.util.interfaces.HttpParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
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

        HttpParser parser = new HttpParserImpl(request, urls);
        parser.parseRequest();
        parser.createResponse();
        System.out.println(parser.sendResponse());
        System.out.println(parser.printCookie());
    }
}
