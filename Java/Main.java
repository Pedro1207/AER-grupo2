import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {

        List<InetAddress> knownAddresses = Collections.synchronizedList(new ArrayList<>());

        MulticastReceiver multicastReceiver = new MulticastReceiver(knownAddresses);
        Thread t = new Thread(multicastReceiver);
        t.start();

        HelloLoop helloLoop = new HelloLoop();
        Thread t2 = new Thread(helloLoop);
        t2.start();

        FilefinderServer filefinderServer = new FilefinderServer(knownAddresses);
        Thread t3 = new Thread(filefinderServer);
        t3.start();


        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), 10000);
        System.out.println(packet.getSocketAddress());


        FileFinder fileFinder = new FileFinder(knownAddresses);
        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String line;
        while(true){
            line = bufferedReader.readLine();
            System.out.println("ola + " + fileFinder.findFile("ola"));
        }


    }
}
