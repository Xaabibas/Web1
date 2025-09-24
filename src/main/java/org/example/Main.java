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
                String request = readRequest();

                HashMap<String, String> params = parse(request);
                response = validator.validate(params);

                String json = String.format(RESULT_JSON, response.isHit());
                makeAndWriteResponse(json);
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

    private static String readRequest() throws IOException, NullPointerException, ValidationException {
        String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        if (!method.equals("POST")) {
            logger.severe("Неподдерживаемый метод");
            throw new ValidationException("Неподдерживаемый метод");
        }
        FCGIInterface.request.inStream.fill();
        int contentLength = FCGIInterface.request.inStream.available();
        ByteBuffer buffer = ByteBuffer.allocate(contentLength);
        int readBytes =
                FCGIInterface.request.inStream.read(buffer.array(), 0,
                        contentLength);
        byte[] requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();
        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }

    private static HashMap<String, String> parse(String jsonStr) throws ValidationException {
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = jsonStr.substring(1, jsonStr.length() - 1).replaceAll("\"", "").split(",");
        if (pairs.length != 3) {
            throw new ValidationException("Некорректное количество аргументов");
        }
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

    private static void makeAndWriteResponse(String json) {
        String response = String.format(HTTP, json.getBytes(StandardCharsets.UTF_8).length, json);
        System.out.println(response);
    }

}