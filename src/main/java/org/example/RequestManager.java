package org.example;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestManager {
    private static final Set<String> availableMethods = new HashSet<>(List.of("POST", "PATCH"));

    public Request readRequest() throws NullPointerException, ValidationException, IOException{
        Request request = new Request();

        String method = readMethod();
        String body = readBody();
        HashMap<String, String> params = parse(body);

        request.setMethod(method);
        request.setParams(params);

        return request;
    }

    private String readMethod() throws NullPointerException, ValidationException {
        String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        if (!availableMethods.contains(method)) {
            Main.logger.severe("Неподдерживаемый метод");
            throw new ValidationException("Неподдерживаемый метод");
        }

        return method;
    }

    private static String readBody() throws IOException, NullPointerException {
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
        if (pairs.length != 4) {
            throw new ValidationException("Некорректное количество аргументов");
        }
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2); // тут что-то не то

            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                params.put(keyValue[0], "");
            }
        }
        return params;
    }
}
