package com.device.app;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

//연결된 안드로이드 디바이스와의 작업을 처리함
public class DeviceAndroid extends Device{

    @Override
    public String getDeviceType() {
        return "Android";
    }

    /**
     * 서버와 주기적으로 통신함
     */
    public void checkDevice() {
        new Thread(() ->{
                try (MulticastSocket multicastSocket = Util.createSocket()){

                    while (true) {
                        byte[] messageBuffer = (deviceName+":AREYOUALIVE").getBytes("UTF-8");
                        DatagramPacket dp = Util.createPacket(messageBuffer, 5554);
                        multicastSocket.send(dp);

                        DatagramPacket receivePacket = Util.createPacket(messageBuffer, 5554);
                        multicastSocket.receive(receivePacket);
                        Thread.sleep(10000);
                    }
                }
                catch(SocketTimeoutException e){
                    e.printStackTrace();
                    System.out.println("서버와 연결 끊김");
                    isDeviceConnect = false;
                }
                 catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
    }
    // 연결된 기기로 이 기기의 정보를 보냅니다



    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public boolean isDeviceConnect() {
        return isDeviceConnect;
    }

    @Override
    public void sendMessageToDevice(Device device, String message) {
        try {
            byte[] messageBuffer = Util.makeMessageBuffer(device, message);
            DatagramPacket dp = Util.createPacket(messageBuffer, 5555);
            Util.createSocket(5555).send(dp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
