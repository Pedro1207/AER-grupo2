import java.net.SocketException;

public class Main2 {

    public static void main(String[] args) throws SocketException {
        Receiver r = new Receiver();
        Thread t = new Thread(r);
        t.start();
    }
}
