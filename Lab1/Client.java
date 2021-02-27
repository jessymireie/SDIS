import java.io.IOException;
import java.net.*;

public class Client {
    public static DatagramSocket socket;
    public static DatagramPacket packet;
    public static void main(String[] args) throws IOException {
        if(args.length<4 && args.length>5 ){
            System.out.println("java Client <host> <port> <oper> <opnd>*");
            return;
        }

        sendRequest(args);
        receiveResponse();
        socket.close();
    }

    private static void sendRequest(String[] args) throws IOException {
         //get info from arguments
         String host = args[0];
         int port=Integer.parseInt(args[1]);
         String oper=args[2].toUpperCase();
         String dnsName = args[3];
 
        
        //send request
        socket= new DatagramSocket();
        String message=new String();
        
        if (oper.equals("LOOKUP"))
           message=oper+" "+dnsName;
        else if (oper.equals("REGISTER")) {
            String ip = args[4];
            message=oper+" "+dnsName+" "+ip;
        } else
            System.exit(2);

        System.out.println("Request: " + message);
        byte[] buf =message.getBytes();
        
        InetAddress address=InetAddress.getByName(host);
        packet=new DatagramPacket(buf, buf.length,address,port);
 
         socket.send(packet);
    }

    private static void receiveResponse() throws IOException {
        //response
        byte[] buffer = new byte[1024];
        packet= new DatagramPacket(buffer, buffer.length);

        //receive response
        socket.receive(packet);
        String response = new String(packet.getData());
        System.out.println("Reply: " + response);
    }

}
