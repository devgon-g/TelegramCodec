package examples.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateGenerator {

    public static String getCurrentDate(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}
