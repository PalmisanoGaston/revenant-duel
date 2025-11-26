package red;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

import Interfaces.GameController;
import escenas.Arena;

public class ServerThread extends Thread {

    private static ServerThread instance;

    private DatagramSocket socket;
    private int serverPort = 5555;
    private boolean end = false;
    private final int MAX_CLIENTS = 2;
    private int connectedClients = 0;
    private ArrayList<Client> clients = new ArrayList<Client>();
    private GameController gameController;
    
    private float[] heroStats = new float[4]; // vida, danio, velocidad, salto
    private int heroVida;
    private int intentosRestantes;
    private Arena serverArena;

    private ServerThread(GameController gameController) {
        this.gameController = gameController;
        if (gameController instanceof Arena) {
            this.serverArena = (Arena) gameController;
        }
        try {
            socket = new DatagramSocket(serverPort);
        } catch (SocketException e) {
//            throw new RuntimeException(e);
        }
    }

    public static ServerThread getInstance(GameController gameController) {
        if (instance == null || instance.end) {
            instance = new ServerThread(gameController);
        }
        return instance;
    }

    public static ServerThread getInstance() {
        return instance;
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
                processMessage(packet);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        } while(!end);
    }

    private void processMessage(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");
        int index = findClientIndex(packet);

        if(parts[0].equals("Connect")){

        	 if(index != -1) {
        	        System.out.println("Client already connected");
        	        this.sendMessage("AlreadyConnected", packet.getAddress(), packet.getPort());
        	        return;
        	    }

        	    if(connectedClients < MAX_CLIENTS) {
        	        connectedClients++;
        	        
        	        // FIRST CLIENT = Hero (role 0), SECOND CLIENT = Boss (role 1)
        	        int assignedRole = connectedClients - 1; // 0 for first, 1 for second
        	        
        	        Client newClient = new Client(assignedRole, packet.getAddress(), packet.getPort()); // Store role in Client
        	        clients.add(newClient);
        	        
        	        // Send the assigned role back to client
        	        sendMessage("Connected:" + assignedRole, packet.getAddress(), packet.getPort());
        	        System.out.println("Client connected as role: " + assignedRole);

        	        if(connectedClients == MAX_CLIENTS) {
        	            for(Client client : clients) {
        	                sendMessage("Start", client.getIp(), client.getPort());
                            Gdx.app.postRunnable(() -> {
                                if (gameController != null)
                                    gameController.startGame();
                            });

                        }
        	        }
        	    } else {
        	        sendMessage("Full", packet.getAddress(), packet.getPort());
        	    }
        } else if(index==-1){
            System.out.println("Client not connected");
            this.sendMessage("NotConnected", packet.getAddress(), packet.getPort());
            return;
        } else {
            switch(parts[0]){
            case "Input":
                // Ensure the message format is correct: "Input:role:keycode"
                if (parts.length >= 3) {
                    int role = Integer.parseInt(parts[1]);
                    int keycode = Integer.parseInt(parts[2]);
                    System.out.println("Server: Received input - Role: " + role + ", Keycode: " + keycode);
                    gameController.accionar(role, keycode);
                } else {
                    System.out.println("Server: Invalid input message format: " + message);
                }
                break;
                case "HeroDied":
                    // Store hero stats and notify both clients
                    this.heroVida = Integer.parseInt(parts[1]);
                    this.intentosRestantes = Integer.parseInt(parts[2]);
                    this.heroStats[0] = Float.parseFloat(parts[3]);
                    this.heroStats[1] = Float.parseFloat(parts[4]);
                    this.heroStats[2] = Float.parseFloat(parts[5]);
                    this.heroStats[3] = Float.parseFloat(parts[6]);
                    
                    
                    if (serverArena != null) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                // Store the current boss health before showing upgrade menu
                                serverArena.storeBossHealth();
                            }
                        });
                    }
                    // Notify hero client to show upgrade menu
                    sendMessage("ShowUpgradeMenu:" + heroVida + ":" + intentosRestantes + ":" + 
                               heroStats[0] + ":" + heroStats[1] + ":" + heroStats[2] + ":" + heroStats[3], 
                               clients.get(0).getIp(), clients.get(0).getPort());
                    
                    // Notify boss client to show waiting screen
                    sendMessage("HeroUpgrading", clients.get(1).getIp(), clients.get(1).getPort());
                    break;
                    
                case "HeroUpgraded":
                    // Update hero stats and resume game
                    this.heroStats[0] = Float.parseFloat(parts[1]);
                    this.heroStats[1] = Float.parseFloat(parts[2]);
                    this.heroStats[2] = Float.parseFloat(parts[3]);
                    this.heroStats[3] = Float.parseFloat(parts[4]);
                    this.intentosRestantes = Integer.parseInt(parts[5]);
                    
                    // Update server game state
                    if (serverArena != null) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                // Store the result of resumeGameWithUpgrades
                                Arena newArena = serverArena.resumeGameWithUpgrades(heroStats[0], heroStats[1], heroStats[2], heroStats[3],intentosRestantes);
                                // Update the game controller reference
                                gameController = newArena;
                                serverArena = newArena;
                            }
                        });
                    }
                    
                    // Notify both clients to resume game
                    sendMessageToAll("ResumeGame:" + heroStats[0] + ":" + heroStats[1] + ":" + 
                                   heroStats[2] + ":" + heroStats[3]);
                    break;

                case "Disconnect":
                    int role = 0;
                    clients.remove(index);
                    connectedClients--;
                    for(Client client : clients) {
                         role = client.getNum();
                    }
                    sendMessageToAll("EndGame:" + role); // Notify remaining client
                    disconnectClients();
                    terminate(); // Terminate thread
                    Gdx.app.postRunnable(() -> {
                        if (gameController != null)
                            gameController.resetAndCreateNewArena();
                    });
                    break;

            }
        }
    }

    public void heroDied(int stat1, int stat2,float stat3,float stat4, float stat5, float stat6) {
    	System.out.println("DEBUG: Processing HeroDied message");
        this.heroVida =stat1;
        this.intentosRestantes = stat2;
        this.heroStats[0] =stat3;
        this.heroStats[1] = stat4;
        this.heroStats[2] =stat5;
        this.heroStats[3] = stat6;

        
        System.out.println("DEBUG: Sending ShowUpgradeMenu to hero client");
        // Notify hero client to show upgrade menu
        sendMessage("ShowUpgradeMenu:" + heroVida + ":" + intentosRestantes + ":" + 
                   heroStats[0] + ":" + heroStats[1] + ":" + heroStats[2] + ":" + heroStats[3], 
                   clients.get(0).getIp(), clients.get(0).getPort());
        
        System.out.println("DEBUG: Sending HeroUpgrading to boss client");
        // Notify boss client to show waiting screen
        sendMessage("HeroUpgrading", clients.get(1).getIp(), clients.get(1).getPort());
    }
    
    
    private int findClientIndex(DatagramPacket packet) {
        int i = 0;
        int clientIndex = -1;
        while(i < clients.size() && clientIndex == -1) {
            Client client = clients.get(i);
            String id = packet.getAddress().toString()+":"+packet.getPort();
            if(id.equals(client.getId())){
                clientIndex = i;
            }
            i++;

        }
        return clientIndex;
    }

    public void sendMessage(String message, InetAddress clientIp, int clientPort) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, clientIp, clientPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void terminate(){
        sendMessageToAll("Disconnect");

        this.end = true;
        socket.close();
        this.interrupt();
    }

    public void sendMessageToAll(String message) {
        for (Client client : clients) {
            sendMessage(message, client.getIp(), client.getPort());
        }
    }

    public void disconnectClients() {
        for (Client client : clients) {
            sendMessage("Disconnect", client.getIp(), client.getPort());
        }
        this.clients.clear();
        this.connectedClients = 0;
    }

    public int getConnectedClientsCount() {
        return connectedClients;
    }
}
