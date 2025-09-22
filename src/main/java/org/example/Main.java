package org.example;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Logger;

public class Main {
    public static final Logger logger = Logger.getLogger("Main");
    private static final Validator validator = new Validator();
    private static final String HTTP = """
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String RESULT_JSON = """
            {
                "result": %b
            }
            """;

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        Response response;

        logger.info("start");

        while (fcgi.FCGIaccept() >= 0) {
            try {
                FCGIInterface.request.inStream.fill();
                var contentLength = FCGIInterface.request.inStream.available();
                var buffer = ByteBuffer.allocate(contentLength);
                var readBytes =
                        FCGIInterface.request.inStream.read(buffer.array(), 0,
                                contentLength);
                var requestBodyRaw = new byte[readBytes];
                buffer.get(requestBodyRaw);
                buffer.clear();
                var request = new String(requestBodyRaw, StandardCharsets.UTF_8);
                logger.info(request);

                // TODO: переделать логику validate
                HashMap<String, String> params = parse(request);
                response = validator.validate(params);

                String json = String.format(RESULT_JSON, response.isHit());
                String result = String.format(HTTP, json.getBytes(StandardCharsets.UTF_8).length + 2, json);

                logger.info(result);
                System.out.println(result);
            } catch (IOException | NullPointerException ignored) {

            }
        }

        logger.info("end");
    }

    private static HashMap<String, String> parse(String jsonStr) {
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = jsonStr.substring(1, jsonStr.length() - 1).replaceAll("\"", "").split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);

            } else {
                params.put(keyValue[0], "");
            }
        }

        return params;
    }



}