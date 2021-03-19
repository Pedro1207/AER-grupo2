import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class Main1 {

    public static void main(String[] args) throws IOException {
        Broadcast b = new Broadcast();

        ArrayList<InetAddress> list = (ArrayList<InetAddress>) b.listAllBroadcastAddresses();
        for(InetAddress i : list){
            System.out.println(i);
            b.broadcast("Hello", i);
        }

    }
}
