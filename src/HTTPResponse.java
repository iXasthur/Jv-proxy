import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HTTPResponse {

    private final BufferedReader reader;
    private final DataOutputStream serverOutputStream;
    private final Socket socket;

    public HTTPResponse(BufferedReader reader, DataOutputStream serverOutputStream, Socket socket) throws IOException {
        this.reader = reader;
        this.serverOutputStream = serverOutputStream;
        this.socket = socket;
    }

    public void sendToBrowser(DataOutputStream browserOutputStream) throws IOException {
        String responseLine = reader.readLine();
        while (responseLine != null) {
//            System.out.println(responseLine);
            browserOutputStream.writeBytes(responseLine + "\r\n");
            browserOutputStream.flush();
            responseLine = reader.readLine();
        }
//        browserOutputStream.writeBytes("\r\n");
//        browserOutputStream.flush();
    }
}