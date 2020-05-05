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
        fixRequestLine();
    }

    private void fixRequestLine() {
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

//            lines.add("Connection: close");

            for (int i = 0; i < lines.size(); i++) {
                if (lines.elementAt(i).contains("Accept-Encoding:")) {
                    lines.set(i, "Accept-Encoding: identity");
                }
            }
        }
    }

    public HTTPResponse sendToHost() throws IOException {

        InetAddress address = InetAddress.getByName(host);

        Socket socket = new Socket(address, port);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        for (String line : lines) {
            System.out.println("REQ > " + line);
            outputStream.writeBytes(line + "\r\n");
            outputStream.flush();
        }
        System.out.println("REQ > ");
        outputStream.writeBytes("\r\n");
        outputStream.flush();

        return new HTTPResponse(reader, outputStream, socket);
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

    //    public void printInfo(){
//        System.out.println();
//        System.out.println("Request: " + requestLine);
//        System.out.println(" Method: " + method);
//        System.out.println("   Path: " + path);
//        System.out.println("   Host: " + host);
//        System.out.println("   Port: " + port);
//        System.out.println("Version: " + version);
//    }


//    Socket socket;
//    DataOutputStream outputStream;
//    BufferedReader reader;


//    HTTPResponse getResponse() throws Exception {
//
//
//        // 1. Get host address using DNS and connect
//
//        InetAddress address = InetAddress.getByName((headers.get("Host")));
//
//        System.out.println("REQ: Connecting to " + address);
//
//        socket = new Socket(address, port);
//        outputStream = new DataOutputStream(socket.getOutputStream());
//        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//        // 2. Send request
//
//        write(method + " " + path + " " + version);
//
//        for(String key : headers.keySet()) {
//            write(key + ": " + headers.get(key));
//        }
//
//        write("");
//
//        // 3. Read response
//
//        HTTPResponse response = new HTTPResponse(reader);
//
//        return response;
//    }
//
//    private void write (String line) throws Exception {
//        System.out.println("REQ > " + line);
//        outputStream.writeBytes(line + "\r\n");
//        outputStream.flush();
//    }
}