package com.example.facelogin.util;

import org.junit.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalIp {
    @Test
    public void ipTest(){
        ip();
    }
    public static String ip() {
        String ipAddress = "";
        try {
            // 获取本机的 InetAddress 对象
            InetAddress localHost = InetAddress.getLocalHost();

            // 获取本机的 IP 地址
            ipAddress = localHost.getHostAddress();
            String hostName = localHost.getHostName();
            System.out.println("本机 "+hostName+ " IP 地址: " + ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }
}
