import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class RunnableMulticast implements Runnable {

    private InetAddress srvc_addr;
    private int srvc_port;
    private InetAddress mcast_addr;
    private int mcast_port;
    private DatagramSocket socketMulticast;
    private String message;

    public RunnableMulticast(InetAddress srvc_addr, int srvc_port, InetAddress mcast_addr, int mcast_port)
            throws SocketException {

        this.srvc_addr = srvc_addr;
        this.srvc_port = srvc_port;
        this.mcast_addr = mcast_addr;
        this.mcast_port = mcast_port;

        this.socketMulticast = new DatagramSocket();
        this.message = new String(srvc_addr + " " + srvc_port);
    }

    @Override
    public void run() {
        byte[] buf = message.getBytes();
        DatagramPacket packetMulticast = new DatagramPacket(buf, buf.length, this.mcast_addr, this.mcast_port);
        try {
            socketMulticast.send(packetMulticast);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("multicast: " + this.mcast_addr.getHostAddress() + " " + this.mcast_port + ": " + this.srvc_addr.getHostAddress() + " " + this.srvc_port);
    }
}