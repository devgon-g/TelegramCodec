package pe.devgon.telegram.decode;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringTelegramResponseSourceTest {

    @Test
    void read_returns_requested_size_when_available() {
        StringTelegramResponseSource source = new StringTelegramResponseSource("HELLO", StandardCharsets.UTF_8);

        assertEquals("HE", source.read(2));
        assertEquals("LL", source.read(2));
        assertEquals("O", source.read(2));
    }

    @Test
    void read_returns_remaining_or_empty() {
        StringTelegramResponseSource source = new StringTelegramResponseSource("AB", StandardCharsets.UTF_8);

        assertEquals("AB", source.read(5));
        assertEquals("", source.read(1));
    }
}
