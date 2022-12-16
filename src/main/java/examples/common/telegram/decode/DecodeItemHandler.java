package examples.common.telegram.decode;

public interface DecodeItemHandler<T> {
    public void handle(T object, TelegramResponseSource source);
}
