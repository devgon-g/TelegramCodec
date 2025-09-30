package pe.devgon.telegram.encode;

import org.junit.jupiter.api.Test;
import pe.devgon.functional.util.Pad;
import pe.devgon.telegram.annotation.CompositeItem;
import pe.devgon.telegram.annotation.FieldItem;
import pe.devgon.telegram.annotation.Filler;
import pe.devgon.telegram.annotation.Protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncodeItemHandlersTest {

    @Test
    void encode_simple_protocol() {
        SampleRequest request = new SampleRequest(12, "PAY", new SampleDetail(7, "OK"));
        StringTelegramRequestSource source = new StringTelegramRequestSource();

        EncodeItemHandlers.of(request).handle(source, request);

        String expected = "0012" + "PAY   " + "007" + "OK   " + "  ";
        assertEquals(expected, source.toString());
    }

    @Protocol(itemCount = 4, fillers = {@Filler(seq = 3, size = 2)})
    private static class SampleRequest {
        @FieldItem(seq = 0, size = 4, pad = Pad.LEFT_ZERO)
        private final int length;

        @FieldItem(seq = 1, size = 6)
        private final String type;

        @CompositeItem(seq = 2)
        private final SampleDetail detail;

        private SampleRequest(int length, String type, SampleDetail detail) {
            this.length = length;
            this.type = type;
            this.detail = detail;
        }
    }

    @Protocol(itemCount = 2)
    private static class SampleDetail {
        @FieldItem(seq = 0, size = 3, pad = Pad.LEFT_ZERO)
        private final int code;

        @FieldItem(seq = 1, size = 5)
        private final String message;

        private SampleDetail(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
