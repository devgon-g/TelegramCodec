package examples.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetUtils {
    public static final InetAddress HOST;

    static {
        try {
            HOST = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            //TODO 시스템 오류처리.
            throw new RuntimeException(e);
        }
    }

    public static String getHostAddress() {
        return HOST.getHostAddress();
    }
}
