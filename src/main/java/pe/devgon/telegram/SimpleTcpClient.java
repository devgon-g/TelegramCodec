package pe.devgon.telegram;

import lombok.extern.slf4j.Slf4j;
import pe.devgon.telegram.decode.StringTelegramResponseSource;
import pe.devgon.telegram.decode.TelegramResponseSource;
import pe.devgon.telegram.encode.TelegramRequestSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SimpleTcpClient implements TcpClient {
    private static final int BUFFER_SIZE = 1024;
    private static final Charset TELEGRAM_CHARSET = StandardCharsets.UTF_8;

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
        try (Socket socket = new Socket(host, port)) {
            log.debug("[{}] connected", socket);

            byte[] payload = requestSource.toString().getBytes(TELEGRAM_CHARSET);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(payload);
            outputStream.flush();
            socket.shutdownOutput();
            log.debug("[{}] send [{}]", socket, requestSource);

            byte[] responseBytes = readAll(socket.getInputStream());
            String responseBody = new String(responseBytes, TELEGRAM_CHARSET);
            log.debug("[{}] receive [{}]", socket, responseBody);

            return new StringTelegramResponseSource(responseBody, TELEGRAM_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[BUFFER_SIZE];
        int read;
        while ((read = inputStream.read(chunk)) != -1) {
            if (read > 0) {
                buffer.write(chunk, 0, read);
            }
        }
        return buffer.toByteArray();
    }

    @Override
    public String getName() {
        return name;
    }
}
