package com.device.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

//명령을 보내는 또다른 프로젝트입니다.
//이 프로그램을 실행할 때 배치파일을 만들고 java -jar receiveCommand.jar 기기이름:명령어 이런식으로 실행하면 해당 기기로 명령이 전달 됩니다.
//그렇게 배치파일을 저장해두면 배치 파일 하나로 사전에 정의한 명령어를 앱에 보낼 수 있습니다. (파워 오토메이트 같은 프로그램을 활용하면 자동으로 명령이 전송 되서 좋음)
public class ReceiveCommand {

    public static void main(String[] args) {
        String device = args[0];
        String deviceCommand= getStringArray(args, 1, args.length, " ");
        System.out.println(device + ":"+deviceCommand);
        try {
            //연결 프로그램과 명령어 전달 프로그램은 따로 분리되어 소켓으로 소통합니다.
            //배치파일 실행 방식을 위해서 따로 분리했습니다.
            Socket socket = new Socket(InetAddress.getByName("localhost"), 55556);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(device+":"+deviceCommand);
            Thread.sleep(1000); //1초 뒤에 성공 했는지 아닌지에 대한 메세지를 알려줍니다.
            System.out.println(dis.readUTF());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStringArray(String[] args, int start, int end, String space){
        StringBuffer buffer = new StringBuffer();
        for(int i = start; i < end;i++){
            buffer.append(args[i]).append(space);
        }
        return buffer.toString();
    }
}
