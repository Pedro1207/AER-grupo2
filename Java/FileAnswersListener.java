import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class FileAnswersListener implements Runnable {


    private ArrayList<FileInfo> fileInfos;
    private long currentTime;
    private String searchTerm;

    public FileAnswersListener(ArrayList<FileInfo> fileInfos, long currentTime, String searchTerm) {
        this.fileInfos = fileInfos;
        this.currentTime = currentTime;
        this.searchTerm = searchTerm;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(10002);
            socket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        byte[] buf = new byte[512];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        FileInfo fi;
        try {
            while(true){
                System.out.println("Waiting for answer!");
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
        System.out.println(received);

        if(received.startsWith("HAVEFILE")) {
            String[] strArray;
            FileInfo fileInfo;
            strArray = received.split(";");
            fileInfo = new FileInfo(this.searchTerm, InetAddress.getByName(strArray[1]), Integer.parseInt(strArray[2]), time - this.currentTime);
            return fileInfo;
        } else{
            return null;
        }

    }

}
