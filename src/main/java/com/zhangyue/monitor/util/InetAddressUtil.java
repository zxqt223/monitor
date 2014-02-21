package com.zhangyue.monitor.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressUtil {

    public static String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }
}
