package examples.common.telegram;

import examples.common.telegram.decode.TelegramResponseSource;
import examples.common.telegram.encode.TelegramRequestSource;

public interface TcpClient {
    TelegramResponseSource send(TelegramRequestSource requestSource);

    public String getName();
}
