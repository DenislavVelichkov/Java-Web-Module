package app;

import app.util.HttpParserImpl;
import app.util.interfaces.HttpParser;

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
        while (true) {
            input = reader.readLine();

            if (input.trim().isEmpty()) {
                request.add(System.lineSeparator());
                input = reader.readLine();
                request.add(input);
                break;
            }

            request.add(input);
        }

        HttpParser parser = new HttpParserImpl(request, urls);
    }
}
