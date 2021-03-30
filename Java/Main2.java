import java.net.SocketException;

public class Main2 {

    public static void main(String[] args) throws SocketException {
        MulticastReceiver r = new MulticastReceiver();
        Thread t = new Thread(r);
        t.start();
    }
}
