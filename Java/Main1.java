import java.io.IOException;


public class Main1 {

    public static void main(String[] args) throws IOException {
        MulticastPublisher b = new MulticastPublisher();
        b.multicast("ola");


    }
}
