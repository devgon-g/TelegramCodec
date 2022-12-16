package examples.common.telegram;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import examples.common.telegram.decode.TelegramResponseSource;
import examples.common.telegram.encode.TelegramRequestSource;
import lombok.extern.slf4j.Slf4j;

import examples.common.telegram.decode.StringTelegramResponseSource;

@Slf4j
public class SimpleTcpClient implements TcpClient {
    private final String name;
    private final String host;
    private final int port;

    public SimpleTcpClient(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public TelegramResponseSource send(TelegramRequestSource requestSource) {

        try (Socket socket = new Socket(host, port)){
        	
            log.debug("[{}] connected.. ", socket);

            new DataOutputStream(socket.getOutputStream())
                    .writeUTF(requestSource.toString());
            log.debug("[{}] send [{}]", socket, requestSource);

            String responseBody = new DataInputStream(socket.getInputStream())
                    .readUTF();
            log.debug("[{}] receive [{}]", socket, responseBody);
            
            return new StringTelegramResponseSource(responseBody.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

}
