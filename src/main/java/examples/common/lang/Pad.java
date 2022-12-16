package examples.common.lang;

import org.apache.commons.lang3.StringUtils;

import java.util.NoSuchElementException;
import java.util.function.Function;

public enum Pad {
    LEFT_SPACE, LEFT_ZERO, RIGHT_SPACE, RIGHT_ZERO;

    public Function<String, String> getFunction(int size){
        switch (this){
            case LEFT_ZERO:
                return value -> StringUtils.leftPad(value, size, '0');
            case LEFT_SPACE:
                return value -> StringUtils.leftPad(value, size, ' ');
            case RIGHT_ZERO:
                return value -> StringUtils.rightPad(value, size, '0');
            case RIGHT_SPACE:
                return value -> StringUtils.rightPad(value, size, ' ');
        }
        throw new NoSuchElementException();
    }
}
