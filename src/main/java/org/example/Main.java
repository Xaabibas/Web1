package org.example;

import com.fastcgi.FCGIInterface;
import org.w3c.dom.ls.LSOutput;

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.logging.Logger;

public class Main {
    public static final Logger logger = Logger.getLogger("Main");
    private static final Validator validator = new Validator();
    private static final String HTTP_RESPONSE = """
            HTTP/1.1 200 OK
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String HTTP_ERROR = """
            HTTP/1.1 400 Bad Request
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String RESULT_JSON = """
            {
                "time": "%s",
                "now": "%s",
                "result": %b
            }
            """;
    private static final String ERROR_JSON = """
            {
                "now": "%s",
                "reason": "%s"
            }
            """;


    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        Response response;
        Console console = System.console();

        logger.info("start");

        while (fcgi.FCGIaccept() >= 0) {
            String jsonString = FCGIInterface.request.params.getProperty("QUERY_STRING");
            Instant start = Instant.now();
            logger.info(jsonString);

            // TODO: переделать логику validate
            HashMap<String, String> params = parse(jsonString);
            response = validate(params);

            Instant end = Instant.now();

            String timePart = String.format(RESULT_JSON, end.getEpochSecond() - start.getEpochSecond(), end.getEpochSecond(), response.isHit());

            logger.info(timePart);
            System.out.println("Content-Type: application/json\n");
            System.out.println(timePart);
        }

        logger.info("end");
    }

    private static HashMap<String, String> parse(String jsonStr) {
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = jsonStr.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                params.put(keyValue[0], "");
            }
        }

        return params;
    }

    private static Response validate(HashMap<String, String> params) {
        Response response = new Response();
        try {
            int x = Integer.parseInt(params.get("x"));
            float y = Float.parseFloat(params.get("y"));
            int r = Integer.parseInt(params.get("r"));

            response.setHit(validator.validate(x, y, r));
        } catch (IllegalArgumentException e) {
            response.setHit(false);
            response.setAnswer("Некорректные данные");
        }
        return response;
    }

}