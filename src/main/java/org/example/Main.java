package org.example;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.logging.Logger;

public class Main {
    private static final FileManager fileManager = new FileManager("path/to/static/data.csv");
    private static final RequestManager requestManager = new RequestManager();
    private static final Processor processor = new Processor();
    public static final Logger logger = Logger.getLogger("Main");

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        Response response = new Response();
        logger.info("start");

        while (fcgi.FCGIaccept() >= 0) {
            try {
                Request request = requestManager.readRequest();

                switch (request.getMethod()) {
                    case "POST":
                        response = processor.process(request);
                        String str = dataToString(request.getParams(), response.isHit(), response.getTime());
                        fileManager.write(str);
                        ResponseManager.makeAndWriteAnswer(response);
                        logger.info("print");
                        break;
                    case "PATCH":
                        fileManager.clear();
                        logger.info("clean");
                        response.setJson("");
                        ResponseManager.makeAndWriteAnswer(response);
                }
            } catch (IOException e) {
                logger.severe("Не удалось получить запрос клиента");
                response = processor.errorResponse(e.getMessage());
                ResponseManager.makeAndWriteAnswer(response);
            } catch (ValidationException e) {
                response = processor.errorResponse(e.getMessage());
                ResponseManager.makeAndWriteAnswer(response);
            } catch (NullPointerException ignored) {

            }
        }
        logger.info("end");
    }

    private static String dataToString(HashMap<String, String> params, boolean result, long time) {
        StringJoiner builder = new StringJoiner(",");
        builder.add(params.get("x")).add(params.get("y")).add(params.get("r"))
                .add(result ? "true" : "false").add(params.get("start")).add(String.valueOf(time));
        return builder.toString();
    }

}