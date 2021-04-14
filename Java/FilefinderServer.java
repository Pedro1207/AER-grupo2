import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FilefinderServer implements Runnable {

    final private List<InetAddress> knowAddresses;
    private InetAddress ownAddress;

    public FilefinderServer(List<InetAddress> knowAddresses, InetAddress ownAddress) {
        this.knowAddresses = knowAddresses;
        this.ownAddress = ownAddress;
    }


    @Override
    public void run() {

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
                interpretPacket(packet, addresses);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void interpretPacket(DatagramPacket packet, ArrayList<InetAddress> addresses) throws IOException {
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println(received);
        String[] strArray = received.split(";");
        if(!strArray[0].equals("s") || Integer.parseInt(strArray[3]) > 0){
            System.out.println("fodeu");
            return;
        }

        Publisher publisher = new Publisher();

        InetAddress returnAddress = null;
        try {
            returnAddress = InetAddress.getByName(strArray[1]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        int fileSize = 10;
        if(/*check file*/fileSize > 5){
            publisher.unicast("HAVEFILE;" + this.ownAddress + ";" + fileSize, returnAddress, 10002);
            System.out.println("oioioi " + returnAddress);
        }

        InetAddress packetAddress = packet.getAddress();
        InetAddress sendAddress;
        for(int i = 0; i < addresses.size(); i++){
            sendAddress = addresses.get(i);
            if(sendAddress != packetAddress){
                publisher.unicast("s;" + returnAddress + ";" + strArray[2] + ";" + (Integer.parseInt(strArray[3]) - 1), sendAddress, 10001);
                System.out.println("aiaiaia " + sendAddress);
            }
        }

    }
}
