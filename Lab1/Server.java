import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server {
    public static HashMap<String, InetAddress> table;

    public Server(int port) {
        table = new HashMap<String, InetAddress>();
    }

    public static int main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("java Server <port number>");
            return -1;
        }

        int port = Integer.parseInt(args[0]);

        DatagramSocket socket = new DatagramSocket(port);

        // start ao server
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        //receive request
        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //process request
            String request = new String(packet.getData());
            System.out.println("Server: " + request);

            String[] message = request.split(" ");
            String ret=new String();


            if (message[0].equals("LOOKUP")) {
                String dnsName = message[1];
                ret=lookup(dnsName);
            } else if (message[0].equals("REGISTER")) {
                String dnsName = message[1];
                InetAddress ip= InetAddress.getByName(message[1]);
                ret=register(dnsName,ip);
            }

            //reply
            socket= new DatagramSocket();
            byte[] buf =ret.getBytes();
            InetAddress address=packet.getAddress();
            DatagramPacket replyPacket=new DatagramPacket(buf, buf.length,address,port);
            socket.send(replyPacket);

        }

    }

    public static String lookup(String dnsName) {
        System.out.println("LOOKUP");
        InetAddress ip = table.get(dnsName);
        return ip == null ? "NOT_FOUND" : ip.toString();
    }

    public static String register(String dnsName, InetAddress ip) {
        System.out.println("REGISTER");
        InetAddress ret = table.put(dnsName,ip);
        return ret==null ? String.valueOf(table.size()) : String.valueOf(-1) ;

    //pesquisar dns name e o ip -> metodo put: "If an existing key is passed then the previous value gets returned. If a new pair is passed, then NULL is returned"
    // se estiver na tabela retorna -1
    //else  number of bindings in the service 
   }
}
