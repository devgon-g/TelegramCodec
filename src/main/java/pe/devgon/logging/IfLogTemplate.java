package pe.devgon.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.devgon.logging.model.IfLog;

/**
 * 간단한 IfLog 기록 유틸리티.
 */
public final class IfLogTemplate {
    private static final Logger logger = LoggerFactory.getLogger(IfLogTemplate.class);

    private IfLogTemplate() {
    }

    public static void write(IfLog ifLog) {
        if (ifLog == null) {
            logger.warn("IfLog is null, skip logging");
            return;
        }
        logger.info("ifLog target={} name={} inOut={} responseCd={} requestBody={} responseBody={}",
                nullSafe(ifLog.getTarget()),
                nullSafe(ifLog.getName()),
                nullSafe(ifLog.getInOut()),
                nullSafe(ifLog.getResponseCd()),
                nullSafe(ifLog.getRequestBody()),
                nullSafe(ifLog.getResponseBody()));
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
