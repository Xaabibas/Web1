package org.example.managers;

import org.example.modules.Response;

import java.nio.charset.StandardCharsets;

public class ResponseManager {
    private static final String HTTP = """
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;

    public static void makeAndWriteAnswer(Response response) {
        String answer = String.format(HTTP, response.getJson().getBytes(StandardCharsets.UTF_8).length, response.getJson());
        System.out.println(answer);
    }
}
