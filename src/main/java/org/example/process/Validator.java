package org.example.process;

import org.example.exceptions.ValidationException;

import java.util.HashMap;

public class Validator {
    public boolean validate(HashMap<String, String> params) throws ValidationException {

        try {
            int x = Integer.parseInt(params.get("x"));
            float y = Float.parseFloat(params.get("y"));
            int r = Integer.parseInt(params.get("r"));

            return checkBox(x, y, r);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Некоректные данные");
        } catch (NullPointerException e) {
            throw new ValidationException("Неверный формат данных");
        }
    }

    private boolean checkBox(int x, float y, int r) {
        if (x >= 0 && y >= 0) {
            return x * x + y * y <= r * r;
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
