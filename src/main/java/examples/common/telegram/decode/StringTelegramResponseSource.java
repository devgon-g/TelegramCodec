package examples.common.telegram.decode;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

@Slf4j
public class StringTelegramResponseSource implements TelegramResponseSource {
    private final ByteBuffer buffer;
    private final String chacterSet;

    public StringTelegramResponseSource(String source) {
        this.buffer = ByteBuffer.wrap(source.getBytes());
        chacterSet = "UTF-8";
    }

    public StringTelegramResponseSource(String source, String chacterSet) {
        try {
            this.buffer = ByteBuffer.wrap(source.getBytes(chacterSet));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.chacterSet = chacterSet;
    }

    @Override
    public String read(int size) {
        if(buffer.remaining() >= size){
            return readString(size);
        }else if (buffer.remaining() == 0){
            log.debug("No data remain");
            return "";
        }else{
            String value = readString(buffer.remaining());
            log.debug("Not enough data remain [{}]", value);
            return value;
        }
    }

    private String readString(int size) {
        byte[] readBytes = new byte[size];
        buffer.get(readBytes);
        return toString(readBytes);
    }

    private String toString(byte[] bytes) {
        try {
            return new String(bytes, chacterSet);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
