package pe.devgon.telegram;

import org.junit.jupiter.api.Test;
import pe.devgon.functional.util.Pad;
import pe.devgon.telegram.annotation.CompositeItem;
import pe.devgon.telegram.annotation.FieldItem;
import pe.devgon.telegram.annotation.Protocol;
import pe.devgon.telegram.decode.StringTelegramResponseSource;
import pe.devgon.telegram.decode.TelegramResponseSource;
import pe.devgon.telegram.encode.TelegramRequestSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TelegramTemplateTest {

    @Test
    void submit_encodes_request_and_decodes_response() {
        String expectedRequest = "0012" + "PAY   " + "007" + "OK   ";
        String responsePayload = "200" + "READY";

        StubTcpClient client = new StubTcpClient(responsePayload);
        TelegramTemplate template = new TelegramTemplate(client);

        TemplateRequest request = new TemplateRequest(12, "PAY", new TemplateDetail(7, "OK"));
        TemplateResponse response = template.submit(request, TemplateResponse.class);

        assertEquals(expectedRequest, client.getLastPayload());
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("READY", response.getMessage().trim());
    }

    private static class StubTcpClient implements TcpClient {
        private final String responsePayload;
        private String lastPayload;

        private StubTcpClient(String responsePayload) {
            this.responsePayload = responsePayload;
        }

        @Override
        public TelegramResponseSource send(TelegramRequestSource requestSource) {
            lastPayload = requestSource.toString();
            return new StringTelegramResponseSource(responsePayload, StandardCharsets.UTF_8);
        }

        @Override
        public String getName() {
            return "stub";
        }

        private String getLastPayload() {
            return lastPayload;
        }
    }

    @Protocol(itemCount = 3)
    private static class TemplateRequest {
        @FieldItem(seq = 0, size = 4, pad = Pad.LEFT_ZERO)
        private final int length;

        @FieldItem(seq = 1, size = 6)
        private final String type;

        @CompositeItem(seq = 2)
        private final TemplateDetail detail;

        private TemplateRequest(int length, String type, TemplateDetail detail) {
            this.length = length;
            this.type = type;
            this.detail = detail;
        }
    }

    @Protocol(itemCount = 2)
    private static class TemplateDetail {
        @FieldItem(seq = 0, size = 3, pad = Pad.LEFT_ZERO)
        private final int code;

        @FieldItem(seq = 1, size = 5)
        private final String message;

        private TemplateDetail(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    @Protocol(itemCount = 2)
    private static class TemplateResponse {
        @FieldItem(seq = 0, size = 3, pad = Pad.LEFT_ZERO)
        private int code;

        @FieldItem(seq = 1, size = 5)
        private String message;

        private int getCode() {
            return code;
        }

        private String getMessage() {
            return message;
        }
    }
}
