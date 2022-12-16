package examples.common.util;

import examples.common.util.sequence.RollingSequenceIdGenerator;
import examples.common.util.sequence.SequenceIdGenerator;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IDGenerator {
    public static final SequenceIdGenerator SEQUENCE_ID_GENERATOR = new RollingSequenceIdGenerator("yyyyMMddHHmmss", 18);
    private static final String SEPARATOR = "_";
    public static String getID(String type, String serviceName) {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(SEPARATOR);
        sb.append(serviceName);
        sb.append(SEPARATOR);
        sb.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        sb.append(SEPARATOR);
        sb.append(RandomStringUtils.randomAlphanumeric(3));
        return sb.toString();
    }

    public static String getOrderNo() {
        StringBuilder sb = new StringBuilder();
        sb.append(RandomStringUtils.randomAlphanumeric(2).toUpperCase());
        sb.append(SEQUENCE_ID_GENERATOR.getNext());
        return sb.toString();
    }

    public static String orderNo() {
        return RandomStringUtils.randomAlphanumeric(20).toUpperCase();
    }

}
