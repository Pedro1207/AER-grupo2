import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FilefinderServer implements Runnable {

    final private List<InetAddress> knowAddresses;
    private final InetAddress ownAddress;
    private FilesChecker filesChecker;

    public FilefinderServer(List<InetAddress> knowAddresses, InetAddress ownAddress, String dataFolder) {
        this.knowAddresses = knowAddresses;
        this.ownAddress = ownAddress;
        this.filesChecker = new FilesChecker(dataFolder);
    }


    @Override
    public void run() {

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(10001);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        byte[] buf = new byte[512];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            while(true){
                socket.receive(packet);
                interpretPacket(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void interpretPacket(DatagramPacket packet) throws IOException {
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println(received);
        String[] strArray = received.split(";");
        if(!strArray[0].equals("s") || Integer.parseInt(strArray[3]) <= 0){
            return;
        }

        ArrayList<InetAddress> addresses = new ArrayList<>();

        synchronized (this.knowAddresses){
            for (InetAddress knownAddress : this.knowAddresses) {
                try {
                    addresses.add(InetAddress.getByName(knownAddress.getHostAddress()));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        Publisher publisher = new Publisher();

        InetAddress returnAddress = null;
        try {
            returnAddress = InetAddress.getByName(strArray[1]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String[] fileInfo = filesChecker.checkForFile(strArray[2]);
        if(fileInfo == null){
            return;
        }
        else{
            publisher.unicast("HAVEFILE;" + this.ownAddress.getHostName() + ";" + fileInfo[0] + ";" + fileInfo[1], returnAddress, 10002);
        }

        InetAddress packetAddress = packet.getAddress();
        InetAddress sendAddress;
        for(int i = 0; i < addresses.size(); i++){
            sendAddress = addresses.get(i);
            if(!sendAddress.equals(packetAddress) && !sendAddress.equals(returnAddress)){
                publisher.unicast("s;" + strArray[1] + ";" + strArray[2] + ";" + (Integer.parseInt(strArray[3]) - 1), sendAddress, 10001);

            }
        }

    }
}
