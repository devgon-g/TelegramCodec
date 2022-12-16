package examples.common.lang;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dates {
    public static String current(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }
}
