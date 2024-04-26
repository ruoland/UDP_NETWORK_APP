package com.device.app;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public abstract class Device {
    private InetAddress curentDeviceAddress;

    protected String deviceName;

    protected Device targetDevice;
    protected boolean isDeviceConnect;
    public abstract String getDeviceName();
    public void sendMessageToDevice(Device device, String message){

    }


    public DatagramPacket receive(){
        try (MulticastSocket multicastSocket = Util.createSocket(5554)) {//5554 포트는 연결 전용 포트입니다
            multicastSocket.setSoTimeout(10000);//10초 이상 연락이 없으면 멈춥니다
            System.out.println("앱에서 오는 응답 기다리는 중...");
            byte[] data = new byte[256];
            DatagramPacket datagramPacket = Util.createPacket(data);
            multicastSocket.receive(datagramPacket);
            String message = new String(data, StandardCharsets.UTF_8);
            System.out.println("받은 메세지:" + message);
            isDeviceConnect = true;//메세지를 받았고 기기 연결 됐다고 설정합니다.
            return datagramPacket;

        } catch (SocketTimeoutException e) {
            System.out.println("앱과 연결 실패, 재시작...");
            try {
                Main.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendDeviceInfo(String message) {
        try {
            MulticastSocket multicastSocketToPC = Util.createSocket();
            byte[] messageBuffer = message.getBytes("UTF-8");
            DatagramPacket dp = Util.createPacket(messageBuffer, 5554);
            multicastSocketToPC.send(dp);
            multicastSocketToPC.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isDeviceConnect(){
        return isDeviceConnect;
    }

    public abstract String getDeviceType();
    public abstract void setDeviceName(String name);

    public void setTargetDevice(){

    }
    public InetAddress getCurrentAddress(){
        return curentDeviceAddress;
    }

    public void setCurentAddress(InetAddress deviceAddress){
        this.curentDeviceAddress = deviceAddress;
    }

    public void setTargetDevice(Device device){
        this.targetDevice = device;
    }

    public Device getTargetDevice(){
        return this.targetDevice;
    }



}
