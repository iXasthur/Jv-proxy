import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        int port = 8080;

        try {
            // The Java runtime automatically closes the input and output streams, the client socket,
            // and the server socket because they have been created in the try-with-resources statement.
            ServerSocket server = new ServerSocket(port);
            System.out.println("Created proxy server on localhost:"+port);

            while (true) {
                Socket socket = server.accept();

                ProxyThread thread = new ProxyThread(socket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
