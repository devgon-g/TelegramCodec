package examples.common.telegram;

import cj.tlj.app.common.logging.IfLogTemplate;
import cj.tlj.app.common.logging.model.IfLog;
import examples.common.telegram.decode.DecodeItemHandlers;
import examples.common.telegram.decode.TelegramResponseSource;
import examples.common.telegram.encode.EncodeItemHandlers;
import examples.common.telegram.encode.StringTelegramRequestSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelegramTemplate {
    private TcpClient client;

    public TelegramTemplate(TcpClient client) {
        this.client = client;
    }

    public <T, R> R submit(T request, Class<R> responseType){
        IfLog.IfLogBuilder ifLogBuilder = startLogging(request);

        try {
            StringTelegramRequestSource source = new StringTelegramRequestSource();
            log.debug("build telegram request - {}", request);
            EncodeItemHandlers.of(request).handle(source, request);
            log.debug("build telegram request - {}", source.toString());
            TelegramResponseSource responseSource = client.send(source);

            log.debug("parse telegram response");
            R response = createResponse(responseType);
            DecodeItemHandlers.of(responseType).handle(response, responseSource);
            log.debug("response - {}", response);

            closeLogging(ifLogBuilder, "200", response.toString());

            return  response;
        } catch (Exception e) {
            closeLogging(ifLogBuilder, "500", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private <T> IfLog.IfLogBuilder startLogging(T request) {
        return IfLog.builder()
                .target(client.getName())
                .name(request.getClass().getSimpleName())
                .inOut("O")
                .requestBody(request.toString());
    }

    private <R> void closeLogging(IfLog.IfLogBuilder ifLogBuilder, String responseCd, String response) {
        ifLogBuilder.responseCd(responseCd)
                .responseBody(response);
        IfLogTemplate.write(ifLogBuilder.build());
    }

    public <R> R createResponse(Class<R> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
