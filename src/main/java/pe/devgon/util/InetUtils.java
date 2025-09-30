package pe.devgon.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetUtils {
    public static final InetAddress HOST;

    static {
        try {
            HOST = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHostAddress() {
        return HOST.getHostAddress();
    }
}
