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

        MulticastReceiver multicastReceiver = new MulticastReceiver(knownAddresses, dataFolder);
        Thread t = new Thread(multicastReceiver);
        t.start();

        InetAddress ownAdress = publisher.getOwnAddress();
        multicastReceiver.setOwnAdrress(ownAdress);

        HelloLoop helloLoop = new HelloLoop();
        Thread t2 = new Thread(helloLoop);
        t2.start();


        FileFinder fileFinder = new FileFinder(knownAddresses, ownAdress);
        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String line;
        while(true){
            System.out.print("Introduce search term: ");
            line = bufferedReader.readLine();
            ArrayList<FileInfo> fileInfos = fileFinder.findFile(line);
            if(fileInfos == null){
                continue;
            } else{
                FileHandler fileHandler = new FileHandler(dataFolder + fileInfos.get(0).getName());
                FileDownloader fd = new FileDownloader(fileInfos, fileHandler);
                fd.download();
            }

        }


    }
}
