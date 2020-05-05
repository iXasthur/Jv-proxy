package jv.proxy.http;

import jv.proxy.utils.BlockedAddresses;
import jv.proxy.utils.ErrorPage;
import jv.proxy.utils.ServerPrinter;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;

public class HTTPRequest {

    private int port = 80;
    private String host = "";
    private String fixedPath = "";

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
                fixedPath = url.getPath();
                host = url.getHost();
                if (url.getPort() != -1) {
                    port = url.getPort();
                }
            } catch (MalformedURLException e) {
                // Do nothing
            }

            requestLine = method + " " + fixedPath + " " + version;

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
        if (!BlockedAddresses.findBlocked(host, fixedPath)) {
            InetAddress address = InetAddress.getByName(host);

            Socket socket = new Socket(address, port);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            ServerPrinter.print(Thread.currentThread().getId(), " Sending " + lines.elementAt(0));

            for (String line : lines) {
                if (line.contains("Host:")) {
                    ServerPrinter.print(Thread.currentThread().getId(), "         " + line + " ...");
                }
//            jv.proxy.utils.ServerPrinter.print(Thread.currentThread().getId(), "         " + line + " ...");
                send(line, outputStream);
            }
            send("", outputStream);

            return new HTTPResponse(inputStream, outputStream, socket);
        } else {
            ServerPrinter.print(Thread.currentThread().getId(), " Blocked " + lines.elementAt(0));

            String errorPage = "HTTP/1.1 403 Forbidden\r\n\r\n" + ErrorPage.getHtmlString(host + fixedPath);

            InputStream is = new ByteArrayInputStream(errorPage.getBytes());
            DataInputStream inputStream = new DataInputStream(is);
            return new HTTPResponse(inputStream, null, null);
        }
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