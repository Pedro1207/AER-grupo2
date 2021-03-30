import java.io.IOException;
import java.net.*;
import java.util.List;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
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
            } else if (received.startsWith("NEWNODE")) {
                registerNewNode(received);
            } else if (received.startsWith("KNOWN")) {
                registerKnown(received);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void registerKnown(String received) throws UnknownHostException {
        String[] strArray = received.split(";");
        synchronized (knownAddresses) {
            for (int i = 1; i < strArray.length; i++) {
                knownAddresses.add(InetAddress.getByName(strArray[i]));
            }
        }
    }

    private void registerNewNode(String received) throws IOException {
        String[] strArray = received.split(";");
        InetAddress address = InetAddress.getByName(strArray[1]);
        synchronized (knownAddresses) {
            if (!this.knownAddresses.contains(address)) {
                publisher.multicast("NEWNODE;" + address.getHostAddress());
                knownAddresses.add(address);
            }
        }
    }

    private void registerAddress(InetAddress address) throws IOException {
        synchronized (knownAddresses) {
            if (!this.knownAddresses.contains(address)) {
                publisher.multicast("NEWNODE;" + address.getHostAddress());

                boolean done = false;
                StringBuilder message = new StringBuilder("KNOWN;");


                knownAddresses.add(address);
                for (int i = 0; i < knownAddresses.size(); i++) {
                    message.append(knownAddresses.get(i).getHostAddress()).append(";");
                    if (i != 0 && i % 5 == 0) {
                        publisher.unicast(message.substring(0, message.length() - 1), address);
                        message = new StringBuilder("KNOWN;");
                    }
                }
                publisher.unicast(message.toString().substring(0, message.length() - 1), address);

            }
        }
    }
}
