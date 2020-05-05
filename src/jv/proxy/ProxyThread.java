package jv.proxy;

import jv.proxy.http.HTTPRequest;
import jv.proxy.http.HTTPResponse;
import jv.proxy.utils.ServerPrinter;

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
                for (String line : request.getLines()) {
                    if (line.contains("Host:")) {
                        ServerPrinter.print(Thread.currentThread().getId(), "         " + line + " ...");
                    }
                }
                request.fixRequestLine();

                HTTPResponse response = request.sendToHost();
                try {
                    response.sendToBrowser(outputStream);
                } finally {
                    response.closeStreams();
                }
            }

        } catch (IOException e) {
            // User closed browser tab
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
