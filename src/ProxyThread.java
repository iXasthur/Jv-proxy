import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ProxyThread extends Thread {

    private Socket socket;

    public ProxyThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        try {
            // Browser -> Proxy
            BufferedReader reader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            // Proxy -> Browser
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            HTTPRequest request = new HTTPRequest(reader);

            if (request.getRequestMethod().equals("GET")) {
                HTTPResponse response = request.sendToHost();
                response.sendToBrowser(outputStream);
            }

            reader.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
