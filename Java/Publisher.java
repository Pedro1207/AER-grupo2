import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

    public void unicast(String message, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        buf = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 10000);
        socket.send(packet);
        socket.close();
    }
}



