import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class HTTPResponse {

    private final DataInputStream inputStream;
    private final DataOutputStream serverOutputStream;
    private final Socket socket;

    public HTTPResponse(DataInputStream inputStream, DataOutputStream serverOutputStream, Socket socket) {
        this.inputStream = inputStream;
        this.serverOutputStream = serverOutputStream;
        this.socket = socket;
    }

    private String extractResponseResult(byte[] bytes, int count) {
        byte[] buffBytes = Arrays.copyOf(bytes, count);
        String buffString = new String(buffBytes);
        Scanner scanner = new Scanner(buffString);
        String retLine = "";
        if (scanner.hasNextLine()) {
            retLine = scanner.nextLine();
        }
        scanner.close();
        return retLine;
    }

    public void sendToBrowser(DataOutputStream browserOutputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int count = inputStream.read(buffer);
        ServerPrinter.print(Thread.currentThread().getId(), "Response " + extractResponseResult(buffer, count));
        ServerPrinter.print(Thread.currentThread().getId(), "         " + "Sending data to browser");
        while (count != -1) {
            send(buffer, count, browserOutputStream);
            count = inputStream.read(buffer);
        }
    }

    public void closeStreams() throws IOException {
        inputStream.close();
        if (serverOutputStream != null) {
            serverOutputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
    }

    private void send(byte[] bytes, int count, DataOutputStream stream) throws IOException {
        stream.write(bytes, 0, count);
        stream.flush();
    }
}