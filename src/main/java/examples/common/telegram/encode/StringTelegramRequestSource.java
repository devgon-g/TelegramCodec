package examples.common.telegram.encode;

public class StringTelegramRequestSource implements TelegramRequestSource{
    private final StringBuilder builder = new StringBuilder();

    public void append(String item) {
        builder.append(item);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
