package pe.devgon.util;

public class XSSEscaper {
    private XSSEscaper() {
    }

    public static String escape(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }
}
