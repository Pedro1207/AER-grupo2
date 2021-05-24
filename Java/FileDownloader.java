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

    public Long download(long initialOffset) {
        DatagramSocket socket = null;
        boolean finished = false;
        long offset = initialOffset;

        try {

            Publisher publisher = new Publisher();
            try {
                socket = new DatagramSocket(10001);
                socket.setSoTimeout(1000);
            } catch (SocketException e) {
                e.printStackTrace();
                return null;
            }

            int messageSize = 50000;

            byte[] buf = new byte[messageSize + 1000];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            int activeHost = 0;
            FileInfo active = this.fileInfos.get(activeHost);

            long size = active.getSize();
            int failCount = 0;



            System.out.println("Size:" + size);

            while (size - offset > messageSize && activeHost < this.fileInfos.size()) {
                try {
                    publisher.unicast("GETCHUNK;" + active.getName() + ";" + offset + ";" + messageSize, active.getLocation(), 10000);
                    socket.receive(packet);
                    writePacketToFile(packet);
                    offset += messageSize;
                    failCount = 0;
                } catch (IOException e) {
                    System.out.println("Failed to get chunck. Retying");
                    failCount++;
                    if(failCount >= 3){
                        activeHost++;
                        if(activeHost < this.fileInfos.size()) active = this.fileInfos.get(activeHost);
                        failCount = 0;
                    }
                }

            }

            while(activeHost < this.fileInfos.size() && !finished){
                try {
                    publisher.unicast("GETCHUNK;" + active.getName() + ";" + offset + ";" + (size - offset), active.getLocation(), 10000);
                    socket.receive(packet);
                    writePacketToFile(packet);
                    finished = true;
                } catch (IOException e) {
                    System.out.println("Failed to get chunck. Retying");
                    failCount++;
                    if(failCount >= 3){
                        activeHost++;
                        if(activeHost < this.fileInfos.size()) active = this.fileInfos.get(activeHost);
                        failCount = 0;
                    }
                }
            }


        } catch (Exception e){
            System.out.println("Error.");
            e.printStackTrace();
            if(socket != null){
                socket.close();
            }
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

        if(!finished) return offset;

        System.out.println("File download finished.");
        return (long) -1;


    }

    private void writePacketToFile(DatagramPacket packet) {
        String received = new String(packet.getData(), 0, packet.getLength());
        if (!received.startsWith("CHUNK")) {
            return;
        }

        String[] strArray = received.split(";", 5);
        try {
            fileHandler.writeBytes(strArray[4].getBytes(StandardCharsets.UTF_8), Integer.parseInt(strArray[2]), Integer.parseInt(strArray[2]) + Integer.parseInt(strArray[3]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
