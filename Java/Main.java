import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        List<InetAddress> knownAddresses = Collections.synchronizedList(new ArrayList<>());

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
