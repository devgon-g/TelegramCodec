package examples.common.telegram;

public class CountHolder {
    private ThreadLocal<Long> count = new ThreadLocal<>();

    public Long get() {
        return count.get();
    }

    public void set(Long value) {
        count.set(value);
    }
}
