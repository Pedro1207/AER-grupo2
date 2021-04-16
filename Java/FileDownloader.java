import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileDownloader {

    private ArrayList<FileInfo> fileInfos;
    private FileHandler fileHandler;

    public FileDownloader(ArrayList<FileInfo> fileInfos, FileHandler fileHandler) {
        this.fileInfos = fileInfos;
        this.fileHandler = fileHandler;
    }

    public void download() {
        DatagramSocket socket = null;
        try {

            Publisher publisher = new Publisher();
            try {
                socket = new DatagramSocket(10001);
                socket.setSoTimeout(5000);
            } catch (SocketException e) {
                e.printStackTrace();
                return;
            }

            byte[] buf = new byte[1500];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            FileInfo active = this.fileInfos.get(0);

            long size = active.getSize();
            long offset = 0;

            while (size - offset > 1000) {
                try {
                    publisher.unicast("GETCHUNK;" + active.getName() + ";" + offset + ";" + size, active.getLocation(), 10000);
                    socket.receive(packet);
                    writePacketToFile(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                offset += 1000;
            }

            try {
                publisher.unicast("GETCHUNK;" + active.getName() + ";" + offset + ";" + (size - offset), active.getLocation(), 10000);
                socket.receive(packet);
                writePacketToFile(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (Exception e){
            if(socket != null){
                socket.close();
            }
        }


    }

    private void writePacketToFile(DatagramPacket packet) {
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println(received);
        if (!received.startsWith("CHUNK")) {
            return;
        }

        String[] strArray = received.split(";");
        try {
            fileHandler.writeBytes(strArray[4].getBytes(StandardCharsets.UTF_8), Integer.parseInt(strArray[2]), Integer.parseInt(strArray[3]) - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
