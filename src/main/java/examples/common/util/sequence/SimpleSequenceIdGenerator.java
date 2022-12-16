package examples.common.util.sequence;

public class SimpleSequenceIdGenerator implements SequenceIdGenerator {
    private long value = 1;
    @Override
    public synchronized String getNext() {
        return String.valueOf(value++);
    }
}
