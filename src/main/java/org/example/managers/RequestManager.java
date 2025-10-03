package org.example.managers;

import com.fastcgi.FCGIInterface;
import org.example.Main;
import org.example.exceptions.ValidationException;
import org.example.modules.Request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestManager {
    private static final Set<String> availableMethods = new HashSet<>(List.of("POST", "PATCH"));
    private static final Set<String> bodyMethods = new HashSet<>(List.of("POST"));

    public Request readRequest() throws NullPointerException, IOException, ValidationException {
        Request request = new Request();

        String method = readMethod();
        request.setMethod(method);

        if (bodyMethods.contains(method)) {
            String body = readBody();
            HashMap<String, String> params = parse(body);
            request.setParams(params);
        }

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

    private static HashMap<String, String> parse(String jsonStr) {
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = jsonStr.substring(1, jsonStr.length() - 1).replaceAll("\"", "").split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);

            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                params.put(keyValue[0], "");
            }
        }
        return params;
    }
}
