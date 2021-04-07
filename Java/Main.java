import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException, SocketException, UnknownHostException {

        List<InetAddress> knownAddresses = Collections.synchronizedList(new ArrayList<>());
        InetAddress myIP;
        DatagramSocket socket = new DatagramSocket();
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        myIP = socket.getLocalAddress();
        System.out.println(myIP);
        socket.close();


        MulticastReceiver multicastReceiver = new MulticastReceiver(knownAddresses);
        Thread t = new Thread(multicastReceiver);
        t.start();

        HelloLoop helloLoop = new HelloLoop();
        Thread t2 = new Thread(helloLoop);
        t2.start();


        while(true){
            System.out.println(knownAddresses);
            Thread.sleep(3000);
        }


    }
}
