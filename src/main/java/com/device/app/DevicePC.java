package com.device.app;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DevicePC extends Device{

    private transient boolean isDeviceConnect;
    private Device targetDevice;

    public DevicePC(){
        setCurentAddress(Util.getThisPC());
        setDeviceName(getCurrentAddress().getHostName());

    }
    public DevicePC(String pcName){
        setDeviceName(pcName);
    }

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
    public String getDeviceType() {
        return "PC";
    }

    @Override
    public void sendMessageToDevice(Device device, String message) {
        try (MulticastSocket multicastSocket = Util.createSocket(5555)) {
            byte[] messageBuffer = message.getBytes("UTF-8");
            DatagramPacket dp = Util.createPacket(messageBuffer, 5555);
            multicastSocket.send(dp);
            multicastSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
