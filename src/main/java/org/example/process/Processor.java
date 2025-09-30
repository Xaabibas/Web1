package org.example.process;

import org.example.exceptions.ValidationException;
import org.example.modules.Request;
import org.example.modules.Response;

public class Processor {
    private static final Validator validator = new Validator();
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

    public Response process(Request request) {
        Response response = new Response();
        try {
            long start = System.nanoTime();
            response.setHit(validator.validate(request.getParams()));
            long end = System.nanoTime();
            response.setTime((end - start) / 1000);
            String json = String.format(RESULT_JSON, response.isHit(), response.getTime());
            response.setJson(json);
        } catch (ValidationException e) {
            response = errorResponse(e.getMessage());
        }

        return response;
    }

    public Response errorResponse(String message) {
        Response response = new Response();
        response.setJson(String.format(ERROR_JSON, message));

        return response;
    }
}
