import java.io.IOException;
import java.net.*;

public class Server {
    public static void main(String[] args) throws SocketException {
        if (args.length != 1) {
            System.out.println("java Server <port number>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        DatagramSocket socket = new DatagramSocket(port);

        // start ao server
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String received = new String(packet.getData());
            System.out.println("Server: "+ received);

            String[] message=received.split(" ");
            if(message[0].equals("lookup")){
                System.out.println("LOOKUP");
            }
            else if(message[0].equals("register")){
                System.out.println("REGISTER");
            }

        }

   }  
}
