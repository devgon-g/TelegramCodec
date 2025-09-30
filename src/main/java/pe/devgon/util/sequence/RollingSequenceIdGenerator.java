package pe.devgon.util.sequence;

import pe.devgon.util.Dates;
import pe.devgon.functional.util.Pad;

public class RollingSequenceIdGenerator implements SequenceIdGenerator {
    private final String dateFormat;
    private final int size;
    private long sequence;
    private String currentDate;

    public RollingSequenceIdGenerator(String dateFormat, int size) {
        this.dateFormat = dateFormat;
        this.size = size;
    }

    @Override
    public synchronized String getNext() {
        String today = Dates.current(dateFormat);
        if (!today.equals(currentDate)) {
            currentDate = today;
            sequence = 0;
        }
        String paddedSequence = Pad.LEFT_ZERO.getFunction(size).apply(String.valueOf(sequence));
        sequence++;
        return currentDate + paddedSequence;
    }
}
