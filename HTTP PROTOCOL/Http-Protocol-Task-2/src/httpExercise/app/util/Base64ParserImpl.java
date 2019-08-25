package httpExercise.app.util;

import httpExercise.app.util.interfaces.Base64Parser;

import java.util.Base64;

public class Base64ParserImpl implements Base64Parser {
    @Override
    public String encodeString(String string) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(string.getBytes());

        return new String(encodedBytes);
    }

    @Override
    public String decodeString(String string) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(string);

        return new String(decodedBytes);
    }
}
