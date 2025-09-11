package org.example;

public class Validator {
    public boolean validate(int x, float y, int r) {
        if (x >= 0 && y >= 0) {
            return x*x + y*y <= r*r;
        }
        if (x < 0 && y >= 0) {
            return x > -r && y < -x;
        }
        if (x >= 0) {
            return x <= r && y <= (float) r / 2;
        }
        return false;
    }
}
