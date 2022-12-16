package examples.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XSSEscaper {
    public static String execute(String value) {
        return value.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]", "");
    }
}
