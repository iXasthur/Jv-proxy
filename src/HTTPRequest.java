import java.io.*;
import java.net.*;
import java.util.Vector;

public class HTTPRequest {

    private int port = 80;
    private String host = "";

    private final Vector<String> lines = new Vector<>(0);

    public HTTPRequest(BufferedReader reader) throws IOException {
        String requestLine;
        while ((requestLine = reader.readLine()) != null && !requestLine.equals("")) {
            lines.add(requestLine);
        }
    }

    public void fixRequestLine() {
        if (lines.size() > 0) {
            String requestLine = lines.elementAt(0);

            String[] split = requestLine.split(" ");
            String method = split[0];
            String path = split[1];
            String version = split[2];

            try {
                URL url = new URL(path);
                path = url.getPath();
                host = url.getHost();
                if (url.getPort() != -1) {
                    port = url.getPort();
                }
            } catch (MalformedURLException e) {
                // Do nothing
            }

            requestLine = method + " " + path + " " + version;

            lines.set(0, requestLine);

            lines.add("Connection: close");

            for (int i = 0; i < lines.size(); i++) {
                if (lines.elementAt(i).contains("Accept-Encoding:")) {
                    lines.set(i, "Accept-Encoding: identity");
                }
            }
        }
    }

    public HTTPResponse sendToHost() throws IOException {

        // Check for blocked address

        InetAddress address = InetAddress.getByName(host);

        Socket socket = new Socket(address, port);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        ServerPrinter.print(Thread.currentThread().getId(), " Sending " + lines.elementAt(0));

        for (String line : lines) {
            if (line.contains("Host:")) {
                ServerPrinter.print(Thread.currentThread().getId(), "         " + line + " ...");
            }
//            ServerPrinter.print(Thread.currentThread().getId(), "         " + line + " ...");
            send(line, outputStream);
        }
        send("", outputStream);

        return new HTTPResponse(inputStream, outputStream, socket);
    }

    private void send(String line, DataOutputStream stream) throws IOException {
        stream.writeBytes(line + "\r\n");
        stream.flush();
    }

    public Vector<String> getLines() {
        return lines;
    }

    public String getRequestMethod() {
        if (lines.size() > 0) {
            String[] split = lines.elementAt(0).split(" ");
            return split[0];
        }
        return "";
    }
}