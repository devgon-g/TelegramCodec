package pe.devgon.functional.util;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public enum Pad implements Function<String, String> {
    LEFT_SPACE(' '),
    LEFT_ZERO('0'),
    RIGHT_SPACE(' '),
    RIGHT_ZERO('0');

    private char padChar;
    private int size = 10;

    Pad(char padChar) {
        this.padChar = padChar;
    }

    @Override
    public String apply(String value) {
        return StringUtils.leftPad(value, size, padChar);
    }

    public Pad size(int size) {
        this.size = size;
        return this;
    }
}
