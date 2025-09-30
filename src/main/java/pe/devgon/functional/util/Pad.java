package pe.devgon.functional.util;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public enum Pad implements Function<String, String> {
    LEFT_SPACE(' ', true),
    LEFT_ZERO('0', true),
    RIGHT_SPACE(' ', false),
    RIGHT_ZERO('0', false);

    private final char padChar;
    private final boolean left;
    private int size = 10;

    Pad(char padChar, boolean left) {
        this.padChar = padChar;
        this.left = left;
    }

    @Override
    public String apply(String value) {
        String input = value == null ? "" : value;
        return left ? StringUtils.leftPad(input, size, padChar)
                : StringUtils.rightPad(input, size, padChar);
    }

    public Pad size(int size) {
        this.size = size;
        return this;
    }

    public Function<String, String> getFunction(int size) {
        return left
                ? value -> StringUtils.leftPad(value == null ? "" : value, size, padChar)
                : value -> StringUtils.rightPad(value == null ? "" : value, size, padChar);
    }
}
