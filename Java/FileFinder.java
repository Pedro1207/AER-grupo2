import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileFinder {

    private final List<InetAddress> knowAddresses;
    private final Publisher publisher;
    private InetAddress ownAddress;

    public FileFinder(List<InetAddress> knowAddresses, InetAddress ownAddress){
        this.knowAddresses = knowAddresses;
        this.publisher = new Publisher();
        this.ownAddress = ownAddress;
    }

    public ArrayList<FileInfo> findFile(String searchTerm) throws IOException {

        ArrayList<InetAddress> addresses = new ArrayList<>();

        synchronized (this.knowAddresses){
            for (InetAddress knownAddress : this.knowAddresses) {
                addresses.add(InetAddress.getByName(knownAddress.getHostAddress()));
            }
        }

        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        FileAnswersListener fal = new FileAnswersListener(fileInfos, System.currentTimeMillis());
        Thread t = new Thread(fal);
        t.start();


        for(int i = 0; i < addresses.size(); i++){
            System.out.println(addresses.get(i));
            publisher.unicast("s;" + this.ownAddress + ";" + searchTerm + ";5", addresses.get(i), 10001);
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return fileInfos;

    }



}
