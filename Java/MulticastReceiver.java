import java.io.IOException;
import java.net.*;
import java.util.List;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[1024];
    private final List<InetAddress> knownAddresses;
    private Publisher publisher;

    public MulticastReceiver(List<InetAddress> knownAddresses) {
        this.knownAddresses = knownAddresses;
        publisher = new Publisher();
    }

    public void run() {
        try {
            socket = new MulticastSocket(10000);
            InetAddress group = InetAddress.getByName("FF7E:230::1234");
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
                this.interpret(received, address);
                if ("end".equals(received)) {
                    break;
                }
            }
            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void interpret(String received, InetAddress address) {

        try {
            if (received.equals("HELLO")) {
                registerAddress(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAddress(InetAddress address) throws IOException {
        synchronized (knownAddresses) {
            if (!this.knownAddresses.contains(address)) {
                knownAddresses.add(address);
            }
        }
    }
}
