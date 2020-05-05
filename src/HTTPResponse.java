import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HTTPResponse {

    private final DataInputStream inputStream;
    private final DataOutputStream serverOutputStream;
    private final Socket socket;

    public HTTPResponse(DataInputStream inputStream, DataOutputStream serverOutputStream, Socket socket) throws IOException {
        this.inputStream = inputStream;
        this.serverOutputStream = serverOutputStream;
        this.socket = socket;
    }

    public void sendToBrowser(DataOutputStream browserOutputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int count = inputStream.read(buffer);
        while (count != -1) {
            send(buffer, count, browserOutputStream);
            count = inputStream.read(buffer);
        }
    }

    synchronized private void send(byte[] bytes, int count, DataOutputStream stream) throws IOException {
        stream.write(bytes, 0, count);
        stream.flush();
    }
}