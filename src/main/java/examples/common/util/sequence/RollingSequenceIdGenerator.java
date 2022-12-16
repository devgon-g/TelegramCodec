package examples.common.util.sequence;

import examples.common.lang.Dates;
import examples.common.lang.Pad;

import java.util.function.Function;

public class RollingSequenceIdGenerator implements SequenceIdGenerator {
    private long value = 1;
    private RollingChecker rollingChecker;
    private String rollingDatePattern;
    private Function<String, String> pad = Pad.LEFT_ZERO.getFunction(4);

    public RollingSequenceIdGenerator(String rollingDatePattern, int size) {
        rollingChecker = new RollingChecker(rollingDatePattern);
        this.rollingDatePattern = rollingDatePattern;
        this.pad = Pad.LEFT_ZERO.getFunction(size - rollingDatePattern.length());
    }

    @Override
    public synchronized String getNext() {
        String current = Dates.current(rollingDatePattern);
        if(rollingChecker.isNeedRolling(current)) value = 1;
        return current + pad.apply(String.valueOf(value++));
    }
}
