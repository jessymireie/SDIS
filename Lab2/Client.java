package Lab2;

import java.io.IOException;
import java.net.*;


public class Client {

    private InetAddress mcast_addr;
    private int mcast_port;
    private String oper;
    private DatagramSocket socket;
    private String dnsName;
    private InetAddress ip;
    private MulticastSocket socketMulticast;
    private static String message;

    public Client(String[] args) throws IOException {
        this.mcast_addr = InetAddress.getByName(args[0]);
        this.mcast_port = Integer.parseInt(args[1]);
        this.oper = args[2].toUpperCase();

        switch (this.oper) {
            case "REGISTER":
                this.dnsName = args[3];
                this.ip = InetAddress.getByName(args[4]);
                message = this.oper + " " + this.dnsName + " " + this.ip;
                break;
            case "LOOKUP":
                this.ip = InetAddress.getByName(args[3]);
                message = this.oper + " " + this.ip;
                break;
            default:
                System.exit(2);
        }

        this.socket = new DatagramSocket();
        
        //create multicast socket
        this.socketMulticast = new MulticastSocket(mcast_port);
        //join group
        this.socketMulticast.joinGroup(this.mcast_addr);
    }

 

    public static void main(String[] args) throws IOException {
        if (args.length < 4 && args.length > 5) {
            System.out.println("java client <mcast_addr> <mcast_port> <oper> <opnd> *");
            System.exit(1);
        }

        Client client = new Client(args);
        InetSocketAddress server=client.awaitServerInfo();
        client.sendRequest(server.getAddress(),server.getPort());
        client.receiveResponse();
    }

    private void sendRequest(InetAddress address,int port) throws IOException {
        System.out.println("Request: " + message);
        byte[] buf = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

        // send request
        this.socket.send(packet);
    }

    private void receiveResponse() throws IOException {
       //response
       byte[] buffer = new byte[1024];
       DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

       //receive response
       socket.receive(packet);
       String response = new String(packet.getData());
       System.out.println("Reply: " + response);
    }

    private InetSocketAddress awaitServerInfo() throws UnknownHostException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {          
            //receive server info
            try {
                this.socketMulticast.receive(packet);
            } catch (Exception e) {
                throw new RuntimeException(e);            
            }

            //parse Info
            String data= new String(packet.getData());
            String[] serverArgs = data.split(" ");
            InetAddress serverAddress=InetAddress.getByName(serverArgs[0]);
            int serverPort=Integer.parseInt(serverArgs[1]);

            System.out.println("multicast: " + this.mcast_addr.getHostAddress() + " " + this.mcast_port+ ": " + serverAddress + " " + serverPort);
            return new InetSocketAddress(serverAddress,serverPort);
        }
 
    }

}
