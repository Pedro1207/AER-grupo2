import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Receiver implements Runnable {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];

    public Receiver() throws SocketException {
        socket = new DatagramSocket(10000);
    }

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            System.out.println("Chegou qq coisa");
        }
        socket.close();
    }
}
