package pe.devgon.util.sequence;

public class SimpleSequenceIdGenerator implements SequenceIdGenerator {
    private final long range;
    private long sequence;

    public SimpleSequenceIdGenerator(long range) {
        this.range = range;
    }

    @Override
    public synchronized String getNext() {
        sequence = (sequence + 1) % range;
        return String.format("%0" + String.valueOf(range).length() + "d", sequence);
    }
}
