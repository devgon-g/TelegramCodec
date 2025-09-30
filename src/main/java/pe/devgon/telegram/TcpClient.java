package pe.devgon.telegram;

import pe.devgon.telegram.decode.TelegramResponseSource;
import pe.devgon.telegram.encode.TelegramRequestSource;

public interface TcpClient {
    TelegramResponseSource send(TelegramRequestSource requestSource);

    String getName();
}
