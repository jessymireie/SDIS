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
         String oper=args[2];
         String opnd = args[3];
 
        
         //send request
         socket= new DatagramSocket();
 
         String message=oper.toUpperCase()+" "+opnd;
         byte[] buf =message.getBytes();
         InetAddress address=InetAddress.getByName(host);
         packet=new DatagramPacket(buf, buf.length,address,port);
 
         socket.send(packet);
    }

    private static void receiveResponse() throws IOException {
        //response
        byte[] buffer = new byte[1024];
        packet= new DatagramPacket(buffer, buffer.length);

        //receive request
        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String request = new String(packet.getData());
            System.out.println("Server: " + request);
        }
    }

}
