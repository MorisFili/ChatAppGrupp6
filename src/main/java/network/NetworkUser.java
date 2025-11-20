package network;

import GUI.ChatWindow;
import javafx.application.Platform;
import model.SystemMessage;
import model.TextNode;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.xml.sax.SAXException;
import session.UserSession;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkUser {
    //Sending and receiving message logic

    private final UserSession userSession;
    private final ChatWindow chatWindow;
    public ExecutorService threadPool = Executors.newCachedThreadPool();
    private final Map<String, Socket> connections = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, PrintWriter> peers = Collections.synchronizedMap(new HashMap<>());
    private ServerSocket server;
    private GatewayDevice device;

    public NetworkUser(UserSession userSession, ChatWindow chatWindow) {
        this.userSession = userSession;
        this.chatWindow = chatWindow;
    }

    // Connecta till server enl uppgifterna från settingwindow
    public void connect() {
        try {
            Socket socket = new Socket(userSession.getIp(), userSession.getTargetPort());
            System.out.println("Successfully connected to: " + userSession.getIp() + ":" + userSession.getTargetPort());
            threadPool.submit(() -> socketHandler(socket));
        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }


    // Gateway checker för UPnP

    private boolean gateway() {

        GatewayDiscover discover = new GatewayDiscover();

        try {
            discover.discover();
            device = discover.getValidGateway();
            if (device != null) {
                String localIp = device.getLocalAddress().getHostAddress();
                int port = userSession.getListenerPort();
                device.addPortMapping(port, port, localIp, "TCP", "ChatApp");
                System.out.println("Mapping complete.");
                System.out.println("Bound to: " + localIp);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Mapping failed.");
            return false;
        }
        System.out.println("Mapping timeout.");
        return false;
    }

    // Statisk "server" som väntar på inkommande uppkopplingar i bakgrunden
    public void server() {
        threadPool.submit(() -> {
            try {
                server = new ServerSocket(userSession.getListenerPort());
                System.out.println("Listener established on port: " + userSession.getListenerPort());

                boolean mapEstablished = gateway();

                while (!server.isClosed() && mapEstablished) {
                    try {
                        Socket socket = server.accept();
                        threadPool.submit(() -> socketHandler(socket));
                    } catch (IOException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Listener terminated.");
        });
    }

    // Hantera alla uppkopplingar
    private void socketHandler(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {


            out.println("OK:" + userSession.getUsername());

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("killswitch")){
                    String[] username = line.split(":");
                    sendSYS(new SystemMessage(userSession.getUsername(), " was kicked by " + username[1]));
                    terminateNetwork();
                }
                if (line.startsWith("OK:")) {
                    String username = line.substring(3);
                    SystemMessage message = new SystemMessage(username, "has connected.");
                    Platform.runLater(() -> chatWindow.getMainBody().getChildren().add(message));
                    peers.put(username, out);
                    connections.put(username, socket);
                }
                if (line.startsWith("SYS:")) {
                    SystemMessage sys = SystemMessage.deserializeMSG(line);
                    Platform.runLater(() -> chatWindow.getMainBody().getChildren().add(sys));
                    if (sys.getContent().contains("disconnected")){
                        connections.get(sys.getUsername()).close();
                        peers.get(sys.getUsername()).close();
                        connections.remove(sys.getUsername());
                        peers.remove(sys.getUsername());
                    }
                }
                if (line.startsWith("MSG:")) {
                    TextNode msg = TextNode.deserializeMSG(line);
                    userSession.getChatLog().add(msg);
                    Platform.runLater(() -> chatWindow.getMainBody().getChildren().add(msg));
                }
            }

            socket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMSG(TextNode msg) {
        for (PrintWriter pw : peers.values()) {
            pw.println(msg.serializeMSG());
        }
    }

    public void sendSYS(SystemMessage sys) {
        for (PrintWriter pw : peers.values()) {
            pw.println(sys.serializeMSG());
        }
    }

    public void sendLine(PrintWriter out, String line){
        if (out == null){
            System.out.println("User already disconnected or you are sending to yourself");
            return;
        }
        out.println(line);
    }

    public void terminateNetwork() throws IOException {
        sendSYS(new SystemMessage(userSession.getUsername(), "has disconnected."));
        System.out.println("Disconnected.");
        if (device != null) {
            try {
                device.deletePortMapping(userSession.getListenerPort(), "TCP");
            } catch (SAXException e) {
                System.out.println(e.getMessage());
            }
        }

        for (Socket connection : connections.values()) {
            connection.close();
        }
        if (server != null) server.close();
    }

    public Map<String, PrintWriter> getPeers() {
        return peers;
    }
}