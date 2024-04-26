package com.device.app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TrayAgain {
    static TrayIcon trayIcon;
    static TrayMenu trayFrame;
    static String toolTip = "명령어 감시중...";
    private static MenuItem statusItem = new MenuItem("기기 찾는 중");

    public static void makeTrayIcon() {
        try {
            trayIcon = new TrayIcon(ImageIO.read(new File("./mark.png")), "트레이 아이콘");
            trayIcon.addActionListener(e -> System.out.println("트레이 아이콘 클릭됨"));
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(toolTip);
            addFrame();
            SystemTray.getSystemTray().add(trayIcon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public static void addFrame(){
        trayFrame = new TrayMenu(trayIcon);
        //기기를 찾고 있는지 상태를 알려줍니다
        trayFrame.addMenu(statusItem, null);

        trayFrame.addItem("연결된 기기", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trayIcon.displayMessage("메세지 전달하기", "안녕!", TrayIcon.MessageType.INFO);
            }
        });
    }

    public static void addDeviceMenu(String menu, Device device){
        PopupMenu popupMenu = new PopupMenu(device.deviceName);
        MenuItem disconnectItem = new MenuItem("연결 끊기");
        disconnectItem.addActionListener(e -> {
            int select = JOptionPane.showConfirmDialog(null, "이 기기와의 연결을 끊을까요?", "기기 연결 끊기", JOptionPane.YES_NO_OPTION);
            if(select == JOptionPane.YES_OPTION){
                Main.findingDevice.removeDevice(device.getDeviceName());
                System.out.println(device.getDeviceName());
            }
        });
        popupMenu.add(disconnectItem);

        MenuItem messageItem = new MenuItem("메세지 보내기");
        messageItem.addActionListener(e -> {
            String text = JOptionPane.showInputDialog(null, device.getDeviceName()+"에게 메세지를 보내기");
            DeviceManager.thisDevice().sendMessageToDevice(device, DeviceManager.thisDevice().getDeviceType()+":"+DeviceManager.thisDevice().getDeviceName()+":"+text);
            System.out.println(device.getDeviceType()+":"+device.getDeviceName()+":"+text);
        });
        popupMenu.add(messageItem);
    }

    public static void setToolTip(String toolTipArg){
        toolTip = toolTipArg;
        trayIcon.setToolTip(toolTip);
    }

    public static void setStatusItem(boolean isFind){
        if(isFind)
            statusItem.setLabel("기기 찾는 중입니다.");
        else
            statusItem.setLabel("기기를 찾고 있지 않습니다.");
    }
}
