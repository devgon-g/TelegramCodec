package pe.devgon.telegram.decode;

import org.junit.jupiter.api.Test;
import pe.devgon.functional.util.Pad;
import pe.devgon.telegram.annotation.CompositeItem;
import pe.devgon.telegram.annotation.FieldItem;
import pe.devgon.telegram.annotation.Filler;
import pe.devgon.telegram.annotation.ListItem;
import pe.devgon.telegram.annotation.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DecodeItemHandlersTest {

    @Test
    void decode_populates_fields() {
        String payload = "0012" + "PAY   " + "007" + "OK   " + "  ";
        StringTelegramResponseSource source = new StringTelegramResponseSource(payload, StandardCharsets.UTF_8);

        SampleResponse response = new SampleResponse();
        DecodeItemHandlers.of(SampleResponse.class).handle(response, source);

        assertEquals(12, response.getLength());
        assertEquals("PAY", response.getType());
        assertNotNull(response.getDetail());
        assertEquals(7, response.getDetail().getCode());
        assertEquals("OK", response.getDetail().getMessage().trim());
    }

    @Test
    void decode_list_field_uses_count() {
        String payload = "07" + "02" + "001020";
        StringTelegramResponseSource source = new StringTelegramResponseSource(payload, StandardCharsets.UTF_8);

        ListContainer container = new ListContainer();
        DecodeItemHandlers.of(ListContainer.class).handle(container, source);

        assertEquals(7, container.getTotal());
        assertEquals(2, container.getEntries().size());
        assertEquals(1, container.getEntries().get(0).getAmount());
        assertEquals(20, container.getEntries().get(1).getAmount());
    }

    @Protocol(itemCount = 4, fillers = {@Filler(seq = 3, size = 2)})
    private static class SampleResponse {
        @FieldItem(seq = 0, size = 4, pad = Pad.LEFT_ZERO)
        private int length;

        @FieldItem(seq = 1, size = 6)
        private String type;

        @CompositeItem(seq = 2)
        private SampleDetail detail;

        public int getLength() {
            return length;
        }

        public String getType() {
            return type;
        }

        public SampleDetail getDetail() {
            return detail;
        }
    }

    @Protocol(itemCount = 2)
    private static class SampleDetail {
        @FieldItem(seq = 0, size = 3, pad = Pad.LEFT_ZERO)
        private int code;

        @FieldItem(seq = 1, size = 5)
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    @Protocol(itemCount = 3)
    private static class ListContainer {
        @FieldItem(seq = 0, size = 2, pad = Pad.LEFT_ZERO)
        private int total;

        @ListItem(countSeq = 1, countSize = 2, countPad = Pad.LEFT_ZERO, dataSeq = 2)
        private List<ListEntry> entries;

        public int getTotal() {
            return total;
        }

        public List<ListEntry> getEntries() {
            return entries;
        }
    }

    @Protocol(itemCount = 1)
    private static class ListEntry {
        @FieldItem(seq = 0, size = 3, pad = Pad.LEFT_ZERO)
        private int amount;

        public int getAmount() {
            return amount;
        }
    }
}
