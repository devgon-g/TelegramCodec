package examples.common.telegram.decode;

public interface TelegramResponseSource {
    public String read(int size);
}
