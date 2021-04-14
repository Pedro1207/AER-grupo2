import java.io.IOException;
import java.net.*;
import java.util.List;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[1024];
    private final List<InetAddress> knownAddresses;
    private Publisher publisher;
    private InetAddress ownAdrress;

    public MulticastReceiver(List<InetAddress> knownAddresses) {
        this.knownAddresses = knownAddresses;
        publisher = new Publisher();
        ownAdrress = null;
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

    public void setOwnAdrress(InetAddress ownAdrress){
        this.ownAdrress = ownAdrress;
    }

    private void interpret(String received, InetAddress address) {

        try {
            if (this.ownAdrress != null && received.equals("HELLO")) {
                registerAddress(address);
            } else if(received.equals("MYADDRESS")){
                giveOwnAddress(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void giveOwnAddress(InetAddress address) {
        try {
            publisher.unicast(address.getHostAddress(), address, 10010);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAddress(InetAddress address) throws IOException {
        synchronized (knownAddresses) {
            if (!this.knownAddresses.contains(address) && !this.ownAdrress.equals(address)) {
                knownAddresses.add(address);
            }
        }
    }
}
