import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server {
    private static Map<String, InetAddress> table;
    private static DatagramSocket socket;
    private static DatagramPacket packet;
    private static int port;

    public Server(int port) throws SocketException {
        table = new HashMap<String, InetAddress>();
        socket = new DatagramSocket(port);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("java Server <port number>");
            System.exit(1);

        }
        port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        server.start();
    }

    private void start() throws IOException {
        while (true) {
            byte[] buffer = new byte[1024];
            packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String request = new String(packet.getData());
            System.out.println("Server: " + request);

            // process request
            String[] message = request.split(" ");
            String ret = new String();
            String oper = message[0];
            String dnsName = message[1];

            if (oper.equals("LOOKUP"))
                ret = lookup(dnsName);
            else if (oper.equals("REGISTER")) {
                InetAddress ip = InetAddress.getByName(message[1]);
                ret = register(dnsName, ip);
            } else
                System.exit(2);

            // reply
            reply(ret, dnsName);
        }
    }

    public static String lookup(String dnsName) {
        if (table.size() == 0)
            return "NOT_FOUND";
        InetAddress ip = table.get(dnsName);
        System.out.println("LOOKUP"+ dnsName+table);
        return ip == null ? "NOT_FOUND" : ip.getHostAddress();
    }

    public static String register(String dnsName, InetAddress ip) {
        InetAddress ret = table.put(dnsName, ip);
        System.out.println("REGISTER "+ table);
        return ret==null ? String.valueOf(table.size()) : String.valueOf(-1) ;
   }

    private void reply(String ret, String dnsName) throws IOException {
        DatagramSocket socketReply= new DatagramSocket();
        String message=ret+" "+dnsName;
        byte[] buf =message.getBytes();
        InetAddress address=packet.getAddress();
        port= packet.getPort();
        DatagramPacket  packetReply=new DatagramPacket(buf, buf.length,address,port);
        socketReply.send(packetReply);
        socketReply.close();        
    }

}
