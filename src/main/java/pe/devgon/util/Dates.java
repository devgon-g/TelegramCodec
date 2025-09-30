package pe.devgon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dates {
    public static String current(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }
}
