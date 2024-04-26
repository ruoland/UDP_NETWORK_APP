package com.device.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

public class TrayMenu extends Frame {
    static  LinkedHashMap<String, MenuItem> itemMap = new LinkedHashMap();

    public TrayMenu(TrayIcon trayIcon){
        if(!SystemTray.isSupported()){
            JOptionPane.showMessageDialog(null, "작동하지 않는 시스템입니다!");
            return;
        }

        MenuItem menuAbout = new MenuItem("이 앱에 대해...");
        menuAbout.addActionListener(e ->{
                JOptionPane.showMessageDialog(null, "이 프로그램은 연결된 기기에서 명령을 받아 처리하거나, 혹은 연결된 기기로 명령을 보내기 위한 프로그램입니다.");
        });

        MenuItem menuExit = new MenuItem("종료...");
        menuExit.addActionListener(e ->{
            System.exit(0);

        });

        trayIcon.setPopupMenu(new PopupMenu());
        getPopupMenu().add(menuAbout);
        getPopupMenu().add(menuExit);
        getPopupMenu().addSeparator();

        trayIcon.setToolTip(trayIcon.getToolTip());
    }

    public void addMenu(MenuItem item, ActionListener actionListener){
        item.addActionListener(actionListener);
        getPopupMenu().add(item);
        TrayAgain.trayIcon.setPopupMenu(getPopupMenu());
    }
    public static PopupMenu getPopupMenu(){
        PopupMenu popupMenu = TrayAgain.trayIcon.getPopupMenu();
        if(popupMenu == null)
            popupMenu  = new PopupMenu();

        return popupMenu;
    }

    public static boolean isTrayOn() {
        return (TrayAgain.getTrayIcon() != null && TrayAgain.getTrayIcon().getPopupMenu() != null);
    }

    public static void addItem(String label, ActionListener action) {
        MenuItem menuItem = new MenuItem(label);
        menuItem.addActionListener(action);
        add(menuItem);
        itemMap.put(label, menuItem);
    }
    public static void addMenu(String label, ActionListener action) {
        MenuItem menuItem = new Menu(label);
        menuItem.addActionListener(action);
        add(menuItem);
        itemMap.put(label, menuItem);
    }
    private static void add(MenuItem item) {
        if (!isTrayOn()) {
            return;
        }
        PopupMenu popupMenu = getPopupMenu();
        popupMenu.add(item);

        TrayAgain.getTrayIcon().setPopupMenu(popupMenu);
    }

    public static LinkedHashMap<String, MenuItem> getItemMap() {
        return itemMap;
    }

    public static void removeDevice(String menu){
        getPopupMenu().remove(getItemMap().get(menu));
        getItemMap().remove(menu);
        TrayAgain.trayIcon.setPopupMenu(getPopupMenu());
    }
}
