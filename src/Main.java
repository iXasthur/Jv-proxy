import jv.proxy.ProxyThread;
import jv.proxy.utils.BlockedAddresses;
import jv.proxy.utils.ErrorPage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        // Set 8080 as default value in IDE

        if (args.length != 1) {
            System.out.println("Invalid args. Args: <port>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid args. Args: <port>");
            return;
        }

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
