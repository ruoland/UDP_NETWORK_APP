package com.device.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DeviceManager {

    //안드로이드와 연결된 기기 맵
    private final static HashMap<String, DeviceAndroid> androidDeviceMap = new HashMap<>();

    //pc와 연결된 기기 맵
    private final static HashMap<String, DevicePC> pcDeviceMap = new HashMap<>();

    //이 앱이 설치된 PC
    private final static DevicePC thisPC = new DevicePC();

    /**
     * 기기 검색을 시작합니다
     */
    public void findDevice() {
        new Thread(() -> {
            try {
                DatagramPacket receive = thisPC.receive();
                if (receive == null) //전달 받은 메세지가 없음
                    return;
                //기기 타입, 이름, 명령을 전달받고 분리합니다.
                String[] deviceTypeNameCommand = Util.splitMessage(Util.getString(receive.getData()));
                String deviceType = deviceTypeNameCommand[0];
                String deviceName = deviceTypeNameCommand[1];
                String command = deviceTypeNameCommand[2];

                //기기에서 보내온 메세지가 "초기 연결"인 경우 서로 연결이 되지 않은 기기로 판단하고, 그 기기의 정보를 저장합니다.
                if (command.equals("초기 연결")) {
                    if(deviceType.equals("Android")) {//안드로이드인 경우 안드로이드로
                        DeviceAndroid deviceAndroid = new DeviceAndroid();
                        deviceAndroid.setDeviceName(deviceName);
                        addDevice(deviceAndroid);
                    }
                    if(deviceType.equals("PC")) {//PC인 경우 PC로
                        DevicePC devicePC = new DevicePC(deviceName);
                        devicePC.setDeviceName(deviceName);
                        addDevice(devicePC);
                    }
                }
                TrayAgain.setStatusItem(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 디바이스 가져오는 메서드입니다.
     */
    public static Device getDevice(String name) {
        if(androidDeviceMap.containsKey(name))
            return androidDeviceMap.get(name);
        else if(pcDeviceMap.containsKey(name))
            return pcDeviceMap.get(name);
        else
            throw new NullPointerException(name+"는 없는 디바이스입니다");
    }

    /**
     * 기기를 추가하면서 지금 이 프로그램이 실행 중인 PC의 정보를 연결된 기기에 보냅니다.
     * 그리고 트레이 아이콘에 이 기기 정보를 추가합니다.
     */
    public void addDevice(Device device) {
        if(device.getDeviceType().equals("Android"))
            androidDeviceMap.put(device.getDeviceName(), (DeviceAndroid) device);
        else if(device.getDeviceType().equals("PC"))
            pcDeviceMap.put(device.getDeviceName(), (DevicePC) device);

        device.sendDeviceInfo(thisDevice().getDeviceType()+":"+thisDevice().getDeviceName() + ":초기 연결");
        System.out.println(device.getDeviceType() +" : " +device.getDeviceName() + "기기와 연결됨");
        TrayAgain.addDeviceMenu(device.getDeviceName(), device);
        saveDevice();
    }

    /**
     * 기기를 제거합니다
     */
    public void removeDevice(String device){
        if (!checkConnection(device)){
            return;
        }
        DeviceAndroid fireDevice = androidDeviceMap.get(device);
        //안드로이드에서 메세지를 수신 받았는데 그게 3234ㅏ9459307*/ 인 경우 기기를 제거하는 걸로 간주합니다. 더 나은 방법이 있겠지만 일단 지금은 그런 식으로 처리합니다.
        DeviceManager.thisDevice().sendMessageToDevice(fireDevice, "이 기기는 해고 되었습니다.3234ㅏ9459307*/");
        System.out.println(fireDevice.getDeviceName() + "와 연결 끊김");
        androidDeviceMap.remove(device);
        TrayMenu.removeDevice(device);
    }

    public static DevicePC thisDevice(){
        return thisPC;
    }

    public void saveDevice() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, DeviceAndroid>>() {
        }.getType();
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream("devices.json"));
            dos.writeUTF(gson.toJson(androidDeviceMap, type));
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDevice() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, DeviceAndroid>>() {
        }.getType();
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("devices.json"));
            gson.fromJson(dis.readUTF(), type);
            dis.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean checkConnection(String androidName){
        return androidDeviceMap.containsKey(androidName) && androidDeviceMap.get(androidName).isDeviceConnect();
    }
}
