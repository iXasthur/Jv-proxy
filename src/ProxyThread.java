import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ProxyThread extends Thread {

    private final Socket socket;

    public ProxyThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            HTTPRequest request = new HTTPRequest(reader);

            if (request.getRequestMethod().equals("GET")) {
                String requestLine = request.getLines().elementAt(0);
                ServerPrinter.print(Thread.currentThread().getId(), "Received " + requestLine);
                request.fixRequestLine();

                HTTPResponse response = request.sendToHost();
                response.sendToBrowser(outputStream);
            }

        } catch (IOException e) {
            // User closed browser tab
//            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
