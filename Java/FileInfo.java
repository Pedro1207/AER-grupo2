import java.net.InetAddress;

public class FileInfo {

    private String name;
    private InetAddress location;
    private long size;
    private long rtt;


    public FileInfo(String name, InetAddress location, long size, long rtt) {
        this.name = name;
        this.location = location;
        this.size = size;
        this.rtt = rtt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getLocation() {
        return location;
    }

    public void setLocation(InetAddress location) {
        this.location = location;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getRtt() {
        return rtt;
    }

    public void setRtt(long rtt) {
        this.rtt = rtt;
    }
}
