package pe.devgon.telegram.decode;

public interface DecodeItemHandler<T> {
    void handle(T object, TelegramResponseSource source);
}
