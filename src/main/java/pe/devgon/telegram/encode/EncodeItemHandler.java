package pe.devgon.telegram.encode;

public interface EncodeItemHandler<T> {
    public void handle(TelegramRequestSource source, T value);
}
