package pe.devgon.telegram.decode;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class StringTelegramResponseSource implements TelegramResponseSource {
    private final ByteBuffer buffer;
    private final Charset charset;

    public StringTelegramResponseSource(String source) {
        this(source, StandardCharsets.UTF_8);
    }

    public StringTelegramResponseSource(String source, Charset charset) {
        this.buffer = ByteBuffer.wrap(source.getBytes(charset));
        this.charset = charset;
    }

    @Override
    public String read(int size) {
        if (buffer.remaining() >= size) {
            return readString(size);
        }
        if (buffer.remaining() == 0) {
            log.debug("No data remain");
            return "";
        }
        String value = readString(buffer.remaining());
        log.debug("Not enough data remain [{}]", value);
        return value;
    }

    private String readString(int size) {
        byte[] readBytes = new byte[size];
        buffer.get(readBytes);
        return new String(readBytes, charset);
    }
}
