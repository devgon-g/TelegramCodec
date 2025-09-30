package pe.devgon.logging.model;

import java.util.Objects;

/**
 * 간단한 인터페이스 로그 모델.
 */
public class IfLog {
    private final String target;
    private final String name;
    private final String inOut;
    private final String requestBody;
    private final String responseCd;
    private final String responseBody;

    private IfLog(IfLogBuilder builder) {
        this.target = builder.target;
        this.name = builder.name;
        this.inOut = builder.inOut;
        this.requestBody = builder.requestBody;
        this.responseCd = builder.responseCd;
        this.responseBody = builder.responseBody;
    }

    public static IfLogBuilder builder() {
        return new IfLogBuilder();
    }

    public String getTarget() {
        return target;
    }

    public String getName() {
        return name;
    }

    public String getInOut() {
        return inOut;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getResponseCd() {
        return responseCd;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return "IfLog{" +
                "target='" + target + '\'' +
                ", name='" + name + '\'' +
                ", inOut='" + inOut + '\'' +
                ", responseCd='" + responseCd + '\'' +
                ", requestBody='" + abbreviate(requestBody) + '\'' +
                ", responseBody='" + abbreviate(responseBody) + '\'' +
                '}';
    }

    private String abbreviate(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() <= 200) {
            return value;
        }
        return value.substring(0, 197) + "...";
    }

    public static class IfLogBuilder {
        private String target;
        private String name;
        private String inOut;
        private String requestBody;
        private String responseCd;
        private String responseBody;

        public IfLogBuilder target(String target) {
            this.target = target;
            return this;
        }

        public IfLogBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IfLogBuilder inOut(String inOut) {
            this.inOut = inOut;
            return this;
        }

        public IfLogBuilder requestBody(String requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public IfLogBuilder responseCd(String responseCd) {
            this.responseCd = responseCd;
            return this;
        }

        public IfLogBuilder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public IfLog build() {
            Objects.requireNonNull(target, "target is required");
            Objects.requireNonNull(name, "name is required");
            Objects.requireNonNull(inOut, "inOut is required");
            return new IfLog(this);
        }
    }
}
