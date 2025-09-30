package org.example;

import com.fastcgi.FCGIInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class Main {
    private static final FileManager fileManager = new FileManager("path/to/static/data.csv");
    private static final RequestManager requestManager = new RequestManager();
    public static final Logger logger = Logger.getLogger("Main");
    private static final Validator validator = new Validator();
    private static final String HTTP = """
            Content-Type: application/json
            Content-Length: %d
            
            %s
            """;
    private static final String RESULT_JSON = """
            {
                "result": %b,
                "time": %d
            }
            """;

    private static final String ERROR_JSON = """
            {
                "error": %s
            }
            """;

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        Response response;
        logger.info("start");

        while (fcgi.FCGIaccept() >= 0) {
            try {
                Request request = requestManager.readRequest();

                switch (request.getMethod()) {
                    case "POST":
                        long start = System.nanoTime();
                        response = validator.validate(request.getParams());
                        long end = System.nanoTime();
                        String json = String.format(RESULT_JSON, response.isHit(), (end - start) / 1000);
                        String str = dataToString(request.getParams(), response.isHit(), (end - start) / 1000);
                        fileManager.write(str);
                        makeAndWriteResponse(json);
                        logger.info("print");
                        break;
                    case "PATCH":
                        fileManager.clear();
                        logger.info("clean");
                        makeAndWriteResponse("");
                }
            } catch (FileNotFoundException | SecurityException e) {
                logger.severe("Проблема с файлом");
            } catch (IOException e) {
                logger.severe("Не удалось получить запрос клиента");
            } catch (ValidationException e) {
                String json = String.format(ERROR_JSON, e.getMessage());
                makeAndWriteResponse(json);
            } catch (NullPointerException ignored) {

            }
        }

        logger.info("end");
    }

    private static void makeAndWriteResponse(String json) {
        String response = String.format(HTTP, json.getBytes(StandardCharsets.UTF_8).length, json);
        System.out.println(response);
    }

    private static String dataToString(HashMap<String, String> params, boolean result, long time) {
        StringJoiner builder = new StringJoiner(",");
        builder.add(params.get("x")).add(params.get("y")).add(params.get("r"))
                .add(result ? "true" : "false").add(params.get("start")).add(String.valueOf(time));
        return builder.toString();
    }

}