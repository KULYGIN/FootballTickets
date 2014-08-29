package HttpLayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//http://localhost:9999/match_id
public class HttpServer implements Runnable {
    private static final int DEFAULT_PORT = 9999;
    @Override
    public void run() {
        int port = DEFAULT_PORT;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + serverSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println("Port " + port + " is blocked.");
            return;
        } 
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept(); 
                ClientSession session = new ClientSession(clientSocket);
                new Thread(session).start();
            } catch (IOException e) {
                System.out.println("Failed to establish connection.");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}