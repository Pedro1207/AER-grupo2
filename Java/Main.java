import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {

        if(args.length < 1){
            System.out.println("Missing arguments. Usage: java Main <dataFolder>");
            System.exit(1);
        } else if(!Files.exists(Path.of(args[0]))){
            System.out.println("Data Folder Does Not Exist.");
            System.exit(2);
        }

        String dataFolder = args[0];
        if(!dataFolder.endsWith("/")){
            dataFolder += "/";
        }

        Publisher publisher = new Publisher();

        List<InetAddress> knownAddresses = Collections.synchronizedList(new ArrayList<>());

        MulticastReceiver multicastReceiver = new MulticastReceiver(knownAddresses);
        Thread t = new Thread(multicastReceiver);
        t.start();

        InetAddress ownAdress = publisher.getOwnAddress();
        multicastReceiver.setOwnAdrress(ownAdress);

        HelloLoop helloLoop = new HelloLoop();
        Thread t2 = new Thread(helloLoop);
        t2.start();

        FilefinderServer filefinderServer = new FilefinderServer(knownAddresses, ownAdress, dataFolder);
        Thread t3 = new Thread(filefinderServer);
        t3.start();

        FileFinder fileFinder = new FileFinder(knownAddresses, ownAdress);
        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String line;
        while(true){
            line = bufferedReader.readLine();
            System.out.println("ola + " + fileFinder.findFile("ola"));
        }


    }
}
