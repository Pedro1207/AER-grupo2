import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) throws UnknownHostException {

        List<InetAddress> knownAddresses = Collections.synchronizedList(new ArrayList<>());
        System.out.println(Inet6Address.getLocalHost());

        MulticastReceiver r = new MulticastReceiver(knownAddresses);
        Thread t = new Thread(r);
        t.start();


    }
}
