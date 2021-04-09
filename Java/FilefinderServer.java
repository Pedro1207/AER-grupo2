import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class FilefinderServer implements Runnable {

    final private ArrayList<InetAddress> knowAddresses;

    public FilefinderServer(ArrayList<InetAddress> knowAddresses) {
        this.knowAddresses = knowAddresses;
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
            socket = new DatagramSocket(10002);
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

    private void interpretPacket(DatagramPacket packet) {
        //do things
    }
}
