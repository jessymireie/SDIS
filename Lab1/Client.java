import java.util.*;
import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        if(args.length!=4){
            System.out.println("java Client <host> <port> <oper> <opnd>*");
            return;
        }

        //get info from arguments
        String host = args[0];
        int port=Integer.parseInt(args[1]);
        String oper=args[2];
        String[] opnd = args[3].split(" ");

       
        //send request
        DatagramSocket socket= new DatagramSocket();

        String message=oper+" "+args[3];
        byte[] buf =message.getBytes();
        InetAddress address=InetAddress.getByName(host);
        DatagramPacket packet=new DatagramPacket(buf, buf.length,address,port);

        socket.send(packet);
         
        //response
        byte[] rbuf=new byte[buf.length];
        packet= new DatagramPacket(rbuf,rbuf.length);
        socket.receive(packet);

        //display response
        String received = new String(packet.getData());
        System.out.println("Echoed Message: "+received);

        //close socket
        socket.close();
 
    }  
}
