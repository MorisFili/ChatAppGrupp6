package network;

import UI.ChatWindow;
import core.Message;
import core.TextMessage;
import javafx.application.Platform;
import service.Commands;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Network {
    //Sending and receiving message logic

    private final UserSession userSession;
    private final ChatWindow chatWindow;
    private final Commands commands;
    public ExecutorService threadPool = Executors.newCachedThreadPool();
    private final Map<String, Socket> connections = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, PrintWriter> peers = Collections.synchronizedMap(new HashMap<>());
    private ServerSocket server;
    private GatewayDevice device;

    public Network(UserSession userSession, ChatWindow chatWindow) {
        this.userSession = userSession;
        this.chatWindow = chatWindow;

        this.commands = new Commands(this);
    }

    // Connecta till server enl uppgifterna från settingwindow
    public void connect() {
        threadPool.submit(() -> {
            try {
                Socket socket = new Socket(userSession.getIp(), userSession.getTargetPort());
                System.out.println("Successfully connected to: " + userSession.getIp() + ":" + userSession.getTargetPort());
                threadPool.submit(() -> socketHandler(socket));
            } catch (IOException e) {
                System.out.println("Connection failed: " + e.getMessage());
            }
        });
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
                String[] args = line.split(":");
                if (args[0].equals("OK")){
                    getPeers().put(args[1], out);
                    getConnections().put(args[1], socket);
                }

                String finalLine = line;
                commands.getCommands().stream().filter(x -> x.getCmd().equals(args[0])).findFirst().ifPresent(x -> commands.inbound(finalLine));
            }

            socket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void send(Message msg) {
        if (msg instanceof TextMessage textMessage){
            userSession.getChatLog().add(textMessage);
            chatWindow.getRepository().saveMessage(textMessage);
            commands.outbound(textMessage);
        } else commands.outbound(msg);
    }

    public void sendLine(String receiver, String line) {
        PrintWriter out = ChatWindow.instance.getNetwork().getPeers().get(receiver);
        if (out == null) {
            System.out.println("User already disconnected or you are sending to yourself");
            return;
        }
        out.println(line);
    }

    public void terminateNetwork() {
        System.out.println("Disconnected.");
        if (device != null) {
            try {
                device.deletePortMapping(userSession.getListenerPort(), "TCP");
                for (Socket connection : connections.values()) connection.close();
                if (server != null) server.close();
            } catch (SAXException | IOException e) {
                System.out.println(e.getMessage());
            }
        }

        threadPool.shutdown();
        chatWindow.getTimer().shutdown();
        Platform.exit();
    }

    public Map<String, PrintWriter> getPeers() {
        return peers;
    }

    public Map<String, Socket> getConnections() {
        return connections;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public ChatWindow getChatWindow() {
        return chatWindow;
    }
}