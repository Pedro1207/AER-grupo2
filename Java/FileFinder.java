import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.SecureRandom;
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
                addresses.add(InetAddress.getByName(knownAddress.getHostName()));
            }
        }

        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        FileAnswersListener fal = new FileAnswersListener(fileInfos, System.currentTimeMillis(), searchTerm);
        Thread t = new Thread(fal);
        t.start();


        SecureRandom sr = new SecureRandom();
        int randomNumber = sr.nextInt();
        if(View.debug) System.out.println("* ***Sending interest packets");
        for(int i = 0; i < addresses.size(); i++){
            if(View.debug) System.out.println("* ***" + "s;" + this.ownAddress.getHostName() + ";" + searchTerm + ";5;" + randomNumber);
            publisher.unicast("s;" + this.ownAddress.getHostName() + ";" + searchTerm + ";5;" + randomNumber, addresses.get(i), 10000);
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<String> uniqueFiles = new ArrayList<>();
        ArrayList<Long> uniqueFilesSize = new ArrayList<Long>();
        for(FileInfo f : fileInfos){
            if(!uniqueFiles.contains(f.getName())){
                uniqueFiles.add(f.getName());
                uniqueFilesSize.add(f.getSize());
            }
        }

        System.out.println("Ficheiros disponíveis:");
        for(int i = 0; i < uniqueFiles.size(); i++){
            System.out.println(i + ": " + uniqueFiles.get(i) + " - " + uniqueFilesSize.get(i) + " bytes");
        }
        System.out.println(uniqueFiles.size() + ": Cancelar");
        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        int file = -1;
        String line;
        while(file < 0 || file > uniqueFiles.size()){
            line = bufferedReader.readLine();
            try{
                file = Integer.parseInt(line);
            } catch (NumberFormatException ignored){

            }

            if(file < 0 || file > uniqueFiles.size()){
                System.out.println("Numero invalido");
            }
        }

        if(file == uniqueFiles.size()){
            return null;
        }

        line = uniqueFiles.get(file);

        ArrayList<FileInfo> choseFiles = new ArrayList<>();
        for(FileInfo f : fileInfos){
            if(f.getName().equals(line)){
                choseFiles.add(f);
            }
        }

        Collections.sort(choseFiles);
        return choseFiles;

    }

    public ArrayList<FileInfo> findExactFile(String searchTerm) throws IOException {

        ArrayList<InetAddress> addresses = new ArrayList<>();

        synchronized (this.knowAddresses){
            for (InetAddress knownAddress : this.knowAddresses) {
                addresses.add(InetAddress.getByName(knownAddress.getHostName()));
            }
        }

        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        FileAnswersListener fal = new FileAnswersListener(fileInfos, System.currentTimeMillis(), searchTerm);
        Thread t = new Thread(fal);
        t.start();

        SecureRandom sr = new SecureRandom();
        int randomNumber = sr.nextInt();


        for(int i = 0; i < addresses.size(); i++){
                if(View.debug) System.out.println("***Sending exact interest packet: " + "es;" + this.ownAddress.getHostName() + ";" + searchTerm + ";5;" + randomNumber);
                publisher.unicast("es;" + this.ownAddress.getHostName() + ";" + searchTerm + ";5;" + randomNumber, addresses.get(i), 10000);
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collections.sort(fileInfos);
        return fileInfos;

    }



}
