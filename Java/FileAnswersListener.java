import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class FileAnswersListener implements Runnable {


    ArrayList<FileInfo> fileInfos;
    long currentTime;

    public FileAnswersListener(ArrayList<FileInfo> fileInfos, long currentTime) {
        this.fileInfos = fileInfos;
        this.currentTime = currentTime;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(10001);
            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        byte[] buf = new byte[512];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        FileInfo fi;
        try {
            while(true){
                socket.receive(packet);
                fi = interpretPacket(packet, System.currentTimeMillis());
                if(fi != null){
                    fileInfos.add(fi);
                }
            }
        } catch (IOException e) {
            System.out.println("No more answers received");
        }
    }


    private FileInfo interpretPacket(DatagramPacket packet, long time) throws UnknownHostException {
        String received = new String(packet.getData(), 0, packet.getLength());

        if(received.startsWith("HAVEFILE")) {
            String[] strArray;
            FileInfo fileInfo;
            strArray = received.split(";");
            fileInfo = new FileInfo(strArray[1], InetAddress.getByName(strArray[2]), Integer.parseInt(strArray[3]), time - this.currentTime);
            return fileInfo;
        } else{
            return null;
        }

    }

}
