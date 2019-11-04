package com.tiza.forward;

import com.tiza.forward.netty.ForwardClient;

/**
 * Description: Boot
 * Author: DIYILIU
 * Update: 2019-11-04 09:35
 */
public class Boot {

    public static void main(String[] args) throws InterruptedException{
        ForwardClient.getInstance().init("192.168.1.32", 8888);

        String str = "192.168.1.32";
        System.out.println(str);
        for (; ; ) {
            ForwardClient.sendMsg(str.getBytes());
            Thread.sleep(2000);
        }
    }
}
