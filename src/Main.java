import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        int port = 8080;

        // The Java runtime automatically closes the input and output streams, the client socket,
        // and the server socket because they have been created in the try-with-resources statement.
        // + JVM/OS will close everything on exit
        try (ServerSocket server = new ServerSocket(port)) {

//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                try {
//                    server.close();
//                    System.out.println("Server on localhost:" + port + " closed");
//                } catch (IOException e) {
//                    /* failed */
//                }
//            }));

            System.out.println("Blocked addresses:");
            BlockedAddresses.update();
            BlockedAddresses.printBlockedAddresses();

            ErrorPage.load();

            System.out.println("Created proxy server on localhost:" + port);

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
