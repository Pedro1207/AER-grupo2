import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileFinder {

    private final List<InetAddress> knowAddresses;

    public FileFinder(List<InetAddress> knowAddresses){
        this.knowAddresses = knowAddresses;
    }

    public List<InetAddress> findFile(String searchTerm) throws UnknownHostException {

        ArrayList<InetAddress> addresses = new ArrayList<>();
        List<InetAddress> possibleAddresses = Collections.synchronizedList(new ArrayList<>());

        synchronized (this.knowAddresses){
            for (InetAddress knownAddress : this.knowAddresses) {
                addresses.add(InetAddress.getByName(knownAddress.getHostAddress()));
            }
        }

        return possibleAddresses;

    }




}
