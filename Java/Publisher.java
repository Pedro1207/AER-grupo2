import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
        socket.send(packet);
        socket.close();
    }

    public InetAddress getOwnAddress() {
        try {
            DatagramSocket socket = new DatagramSocket(10010);
            byte[] buf = new byte[100];
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getLoopbackAddress(), 10010);
            socket.send(packet);
            socket.receive(packet);
            System.out.println(packet.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}



