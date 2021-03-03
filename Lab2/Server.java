package Lab2;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.crypto.Data;

public class Server {
    private DatagramSocket socket;
    private HashMap<String, InetAddress> table;
    private int srvc_port;
    private InetAddress mcast_addr;
    private int mcast_port;
    private DatagramPacket packet;

    public Server(int srvc_port, InetAddress mcast_addr,int mcast_port) throws SocketException {
        this.table = new HashMap<String, InetAddress>();
        this.srvc_port=srvc_port;
        this.mcast_addr=mcast_addr;
        this.mcast_port=mcast_port;

        this.socket = new DatagramSocket(srvc_port);
    }

    public static void main(String[] args) throws IOException {
        if(args.length<3){
            System.out.println("java Server <srvc_port> <mcast_addr> <mcast_port>");
            System.exit(1);
        }

        //parse Args
        int srvc_port = Integer.parseInt(args[0]);
        InetAddress mcast_addr= InetAddress.getByName(args[0]);
        int mcast_port = Integer.parseInt(args[1]);

        //Start
        Server server= new Server(srvc_port,mcast_addr,mcast_port);
        server.startMulticast();
        server.start();
    }

    private void start() throws IOException {
        while (true) {
            byte[] buffer = new byte[1024];
            this.packet = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String request = new String(packet.getData());
            System.out.println("Server: " + request);

            // process request
            String[] message = request.split(" ");
            String oper = message[0];
            String dnsName = message[1];

            String ret = new String();

            switch(oper){
                case "REGISTER":
                    InetAddress ip = InetAddress.getByName(message[1]);
                    ret = register(dnsName, ip);
                    break;
                case "LOOKUP":
                    ret = lookup(dnsName);
                    break;
                default:
                    System.exit(2);
            }
    
            // reply
            reply(ret, dnsName);
        }
    }

    private String register(String dnsName, InetAddress ip) {
        InetAddress success = this.table.put(dnsName,ip);
        return success == null ? String.valueOf(this.table.size()) : String.valueOf(-1);
    }

    private String lookup(String dnsName) {
        InetAddress ip = this.table.get(dnsName);
        return ip == null ? "NOT_FOUND" : ip.getHostAddress();
    }

    private void reply(String ret, String dnsName) throws IOException {
        String message=ret+" "+dnsName;
        InetAddress address=packet.getAddress();
        int port= packet.getPort();
        byte[] buf =message.getBytes();

        DatagramSocket socketReply= new DatagramSocket();
        DatagramPacket  packetReply=new DatagramPacket(buf, buf.length,address,port);
        
        socketReply.send(packetReply);
        socketReply.close();        
    }

    private void startMulticast() throws SocketException, UnknownHostException {
        final Runnable multicast=  new RunnableMulticast(InetAddress.getByName("localhost"),this.srvc_port,this.mcast_addr,this.mcast_port);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //ScheduledFuture<?> schedulerHandle = 
        scheduler.scheduleAtFixedRate(multicast, 1, 1, TimeUnit.SECONDS);
    }


}
