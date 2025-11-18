package network;

import GUI.ChatWindow;
import javafx.application.Platform;
import model.TextNode;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.xml.sax.SAXException;
import session.UserSession;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkUser {
    //Sending and receiving message logic

    private final UserSession userSession;
    private final ChatWindow chatWindow;
    public ExecutorService threadPool = Executors.newCachedThreadPool();
    private final List<Socket> connections = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, PrintWriter> peers = Collections.synchronizedMap(new HashMap<>());
    private ServerSocket server;
    private GatewayDevice device;

    public NetworkUser(UserSession userSession, ChatWindow chatWindow) {
        this.userSession = userSession;
        this.chatWindow = chatWindow;
    }

    // Connecta till server enl uppgifterna från settingwindow
    public void connect() {
        if (userSession.getIp().startsWith("0")) return; // Skippa om default

        try {
            Socket socket = new Socket(userSession.getIp(), userSession.getTargetPort());
            connections.add(socket);
            threadPool.submit(() -> socketHandler(socket));
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    // Gateway checker för UPnP

    private void gateway() {
        try {
            GatewayDiscover discover = new GatewayDiscover();
            discover.discover();
            device = discover.getValidGateway();
            if (device != null) {
                String localIp = device.getLocalAddress().getHostAddress();
                int port = userSession.getListenerPort();
                device.addPortMapping(port, port, localIp, "TCP", "ChatApp");
            }
        } catch (Exception ignored) {
        }
    }


    // Statisk "server" som väntar på inkommande uppkopplingar i bakgrunden
    public void server() {
        threadPool.submit(() -> {
            try {
                server = new ServerSocket(userSession.getListenerPort());

                gateway();

                while (!server.isClosed()) {
                    try {
                        Socket socket = server.accept();
                        connections.add(socket);
                        threadPool.submit(() -> socketHandler(socket));
                    } catch (IOException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Hantera alla uppkopplingar
    private void socketHandler(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {


            out.println("OK:" + userSession.getUsername()); // Skicka substring + username till mottagaren vid uppkoppling

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("OK:")) {
                    String username = line.substring(3);
                    peers.put(username, out); // lagra username + printwriter i en hashmap
                    TextNode message = new TextNode(username, LocalDateTime.now(), "User has connected.");
                    sendMSG(message);
                    //Platform.runLater(() -> chatWindow.getMainBody().getChildren().add(message));
                }
                if (line.startsWith("MSG:")) { // meddelanden startar med MSG för att kunna urskilja
                    TextNode msg = TextNode.deserializeMSG(line);
                    userSession.getChatLog().add(msg);
                    Platform.runLater(() -> chatWindow.getMainBody().getChildren().add(msg));
                }
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMSG(TextNode msg) {
        // skickar till alla per default, ändra logik här
        for (PrintWriter pw : peers.values()) {
            pw.println(msg.serializeMSG());
        }
    }

    public void terminateNetwork() throws IOException {
        try {
            device.deletePortMapping(userSession.getListenerPort(), "TCP");
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        }

        for (Socket connection : connections) {
            connection.close();
        }
        if (server != null) server.close();

    }

}
