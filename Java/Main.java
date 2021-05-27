import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Main {

    /**
     * Variável que guarda o número a partir do qual os
     * hosts serão descartados, caso não sejam recebidos hello's
     */
    private static final int MAX_TIMES_WITHOUT_HELLO = 3;

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Missing arguments. Usage: java Main <dataFolder>");
            System.exit(1);
        } else if (!Files.exists(Path.of(args[0]))) {
            System.out.println("Data Folder Does Not Exist.");
            System.exit(2);
        }

        String dataFolder = args[0];
        if (!dataFolder.endsWith("/")) {
            dataFolder += "/";
        }

        Publisher publisher = new Publisher();

        List<InetAddress> knownAddresses = Collections.synchronizedList(new ArrayList<>());
        List<Integer> dropControlList = Collections.synchronizedList(new ArrayList<>());
        ArrayList<RandomNumberSaver> randomNumberSavers = new ArrayList<>();

        MulticastReceiver multicastReceiver = new MulticastReceiver(knownAddresses, dropControlList, dataFolder, randomNumberSavers);
        Thread t = new Thread(multicastReceiver);
        t.start();

        InetAddress ownAdress = publisher.getOwnAddress();
        multicastReceiver.setOwnAdrress(ownAdress);

        HelloLoop helloLoop = new HelloLoop(MAX_TIMES_WITHOUT_HELLO, knownAddresses, dropControlList, randomNumberSavers);
        Thread t2 = new Thread(helloLoop);
        t2.start();


        FileFinder fileFinder = new FileFinder(knownAddresses, ownAdress);
        String line;

        int opcao = 1;
        while (opcao != 2) {
            opcao = View.menu();
            if (opcao == 1) {
                pesquisar(dataFolder, fileFinder);
            }
        }
        System.out.println("Obrigado por usar o P2P-Network!!!");
        System.exit(0);
    }

    private static void pesquisar(String dataFolder, FileFinder fileFinder) throws IOException {
        String line;
        line = View.menuPesquisa();
        boolean done = false;

        while (!done) {
            ArrayList<FileInfo> fileInfos = fileFinder.findFile(line);

            if(fileInfos == null){
                done = true;
            }

            else if (fileInfos.size() > 0) {
                line = fileInfos.get(0).getName();
                FileHandler fileHandler = new FileHandler(dataFolder + fileInfos.get(0).getName());
                FileDownloader fd = new FileDownloader(fileInfos, fileHandler);
                long offset = fd.download(0);

                while (offset >= 0) {
                    fileInfos = fileFinder.findExactFile(line);
                    fd = new FileDownloader(fileInfos, fileHandler);
                    offset = fd.download(offset);
                }

                done = true;
            }
        }
    }
}
