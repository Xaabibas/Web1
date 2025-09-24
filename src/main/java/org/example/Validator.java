package org.example;

import java.util.HashMap;

public class Validator {
    public Response validate(HashMap<String, String> params) throws ValidationException{
        Response response = new Response();
        try {
            int x = Integer.parseInt(params.get("x"));
            float y = Float.parseFloat(params.get("y"));
            int r = Integer.parseInt(params.get("r"));

            response.setHit(checkBox(x, y, r));
        } catch (IllegalArgumentException e) {
            response.setHit(false);
            throw new ValidationException("Некоректные данные");
        } catch (NullPointerException e) {
            throw new ValidationException("Неверный формат данных");
        }
        return response;
    }

    private boolean checkBox(int x, float y, int r) {
        if (x >= 0 && y >= 0) {
            return x*x + y*y <= r*r;
        }
        if (x < 0 && y >= 0) {
            return x >= -r && y <= r - x;
        }
        if (x >= 0) {
            return x <= r && y >= (float) -r / 2;
        }
        return false;
    }
}
