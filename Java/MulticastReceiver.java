import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[1024];
    private final List<InetAddress> knownAddresses;
    private final Publisher publisher;
    private InetAddress ownAdrress;
    private final FilesChecker filesChecker;
    private String dataFolder;

    public MulticastReceiver(List<InetAddress> knownAddresses, String dataFolder) {
        this.knownAddresses = knownAddresses;
        publisher = new Publisher();
        ownAdrress = null;
        this.filesChecker = new FilesChecker(dataFolder);
        this.dataFolder = dataFolder;
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
            } else if(received.startsWith("s")){
                interpretPacket(received, address);
            } else if(received.startsWith("GETCHUNK")){
                sendChunk(received, address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendChunk(String received, InetAddress address) {
        String[] strArray = received.split(";");

        FileHandler fileHandler = new FileHandler(dataFolder + strArray[1]);

        try {
            byte[] bytes = fileHandler.readBytes(Integer.parseInt(strArray[2]), Integer.parseInt(strArray[3]));
            publisher.unicast("CHUNK;" + strArray[1] + ";" + strArray[2] + ";" + strArray[3] + ";" + Arrays.toString(bytes), address, 10001);
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


    private void interpretPacket(String received, InetAddress packetAddress) throws IOException {
        System.out.println(received);
        String[] strArray = received.split(";");
        if(Integer.parseInt(strArray[3]) <= 0){
            return;
        }

        ArrayList<InetAddress> addresses = new ArrayList<>();

        synchronized (this.knownAddresses){
            for (InetAddress knownAddress : this.knownAddresses) {
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
            publisher.unicast("HAVEFILE;" + this.ownAdrress.getHostName() + ";" + fileInfo[0] + ";" + fileInfo[1], returnAddress, 10002);
        }

        InetAddress sendAddress;
        for(int i = 0; i < addresses.size(); i++){
            sendAddress = addresses.get(i);
            if(!sendAddress.equals(packetAddress) && !sendAddress.equals(returnAddress)){
                publisher.unicast("s;" + strArray[1] + ";" + strArray[2] + ";" + (Integer.parseInt(strArray[3]) - 1), sendAddress, 10000);

            }
        }

    }


}
