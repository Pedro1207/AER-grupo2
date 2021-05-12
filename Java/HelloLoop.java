import java.io.IOException;

public class HelloLoop extends Thread {

    Publisher publisher = new Publisher();

    public void run() {


        while(true){
            try {
                publisher.multicast("HELLO");
                Thread.sleep(3000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
