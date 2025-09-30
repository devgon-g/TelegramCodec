package pe.devgon.telegram.decode;

public interface TelegramResponseSource {
    String read(int size);
}
