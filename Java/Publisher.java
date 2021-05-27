import java.io.IOException;
import java.net.*;

public class Publisher {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;


    public void multicast(String multicastMessage) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName("FF7E:230::1234");
        buf = multicastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 10000);
        socket.send(packet);
        socket.close();
    }

    public void unicast(String message, InetAddress address, int port) throws IOException {
        socket = new DatagramSocket();
        buf = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try{
            socket.send(packet);
        } catch (Exception e){
            //Do nothing, host went offline
        }
        socket.close();
    }

    public InetAddress getOwnAddress() throws IOException {
        DatagramSocket socket = new DatagramSocket(10010);
        socket.setSoTimeout(500);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        boolean found = false;
        InetAddress address = null;

        while(!found){
            try{
                this.multicast("MYADDRESS");
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                address = InetAddress.getByName(received);
                found = true;
            } catch (SocketTimeoutException | UnknownHostException e){
                System.out.println("Trying again.");
            }
        }

        return address;
    }
}



