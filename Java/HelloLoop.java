import java.io.IOException;
import java.net.InetAddress;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class HelloLoop extends Thread {

    private final Publisher publisher = new Publisher();
    
    /**
     * Estrutura que guarda os hosts que cada peer
     * mantém conectividade de momento
     */
    private final List<InetAddress> knownAddresses;
    
    /**
     * Estrutura que vai servir para controlar o drop
     * de um host que não tenha enviado HELLO's num
     * determinado número de segundos
     */
    private final List<Integer> dropControlList;
    
    /**
     * Variável que guarda o número a partir do qual os
     * hosts serão descartados, caso não sejam recebidos hello's
     */
    private int max_times_without_hello;

    private final ArrayList<RandomNumberSaver> randomNumberSavers;
    
    public HelloLoop(int max_times_without_hello, List<InetAddress> knownAddresses, List<Integer> dropControlList, ArrayList<RandomNumberSaver> randomNumberSavers){
        
        this.max_times_without_hello = max_times_without_hello;
        this.knownAddresses = knownAddresses;
        this.dropControlList = dropControlList;
        this.randomNumberSavers = randomNumberSavers;
    }
    

    public void run() {

        while(true){
            try {
                publisher.multicast("HELLO");

                synchronized (this.knownAddresses){

                    for(int i = 0; i<this.knownAddresses.size(); i++){
                        /* Incrementamos 1 valor a cada um dos */
                        this.dropControlList.set(i,this.dropControlList.get(i) + 1);
                        if(this.dropControlList.get(i) >= this.max_times_without_hello) {
                            this.knownAddresses.remove(i);
                            this.dropControlList.remove(i);
                            --i;
                        }
                    }

                    for(int i = this.randomNumberSavers.size() - 1; i >= 0; i--){
                        this.randomNumberSavers.get(i).reduceOne();
                        if(this.randomNumberSavers.get(i).getTtl() <= 0){
                            this.randomNumberSavers.remove(i);
                        }
                    }
                }

                if(View.hostDebug) System.out.println("Hosts conhecidos: " + knownAddresses);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
