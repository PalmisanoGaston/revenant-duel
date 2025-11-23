package red;

import java.io.IOException;
import java.net.*;

import com.badlogic.gdx.Gdx;

import Interfaces.GameController;

public class ClientThread extends Thread {

    private static ClientThread instance;

    private DatagramSocket socket;
    private int serverPort = 5555;
    private String ipServerStr = "255.255.255.255"; // Change to actual server IP
    private InetAddress ipServer;
    private boolean end = false;
    private GameController gameController;

    private ClientThread(GameController gameController) {
        try {
            this.gameController = gameController;
            ipServer = InetAddress.getByName(ipServerStr);
            socket = new DatagramSocket();
            registerShutdownHook();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!end) {
                System.out.println("Application closing - disconnecting client...");
                try {
                    sendMessage("Disconnect");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public static ClientThread getInstance(GameController gameController) {
        if (instance == null || instance.end) {
            instance = new ClientThread(gameController);
        }
        return instance;
    }

    public static ClientThread getInstance() {
        return instance;
    }

    public static boolean isActive() {
        return instance != null && !instance.end;
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
                processMessage(packet);
            } catch (SocketException e) {
                // ✅ NUEVO: Manejar cierre de socket limpiamente
                if (!end) {
                    System.out.println("Connection lost - socket closed");
                    // Notificar al game controller que se perdió conexión
                    if (gameController instanceof escenas.Arena) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                ((escenas.Arena) gameController).onServerDisconnected();
                            }
                        });
                    }
                }
                break; // Salir del loop
            } catch (IOException e) {
                if (!end) {
                    e.printStackTrace();
                }
            }
        } while(!end);
    }


    private void processMessage(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");

        System.out.println("Mensaje recibido: " + message);

        switch(parts[0]){
            case "AlreadyConnected":
                System.out.println("Ya estas conectado");
                break;
            case "Connected":
                // STORE THE ASSIGNED ROLE!
                int assignedRole = Integer.parseInt(parts[1]);
                System.out.println("Conectado al servidor como rol: " + assignedRole);
                this.ipServer = packet.getAddress();
                
                // Notify the game controller about the assigned role
                if (gameController instanceof escenas.Arena) {
                    ((escenas.Arena) gameController).setPlayerRole(assignedRole);
                }
                break;
            case "Full":
                System.out.println("Servidor lleno");
                this.end = true;
                break;
            case "Start":
                this.gameController.startGame();
                break;
            case "ShowUpgradeMenu":
                System.out.println("DEBUG: Received ShowUpgradeMenu message");
                // Parse hero stats and show upgrade menu
                int heroVida = Integer.parseInt(parts[1]);
                int intentosRestantes = Integer.parseInt(parts[2]);
                float multVida = Float.parseFloat(parts[3]);
                float multDanio = Float.parseFloat(parts[4]);
                float multVelocidad = Float.parseFloat(parts[5]);
                float multSalto = Float.parseFloat(parts[6]);
                
                System.out.println("DEBUG: Calling gameController.heroDied()");
                
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gameController.heroDied(heroVida, intentosRestantes, multVida, multDanio, multVelocidad, multSalto);
                    }
                });
                break;
                
            case "HeroUpgrading":
            	 Gdx.app.postRunnable(new Runnable() {
            	        @Override
            	        public void run() {
            	            gameController.heroDied(0, 0, 0, 0, 0, 0);
            	        }
            	    });
                break;
                
            case "ResumeGame":
                // Resume game with updated stats
                float newMultVida = Float.parseFloat(parts[1]);
                float newMultDanio = Float.parseFloat(parts[2]);
                float newMultVelocidad = Float.parseFloat(parts[3]);
                float newMultSalto = Float.parseFloat(parts[4]);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gameController.heroUpgraded(newMultVida, newMultDanio, newMultVelocidad, newMultSalto);
                    }
                });
                break;

            case "State":
                if (gameController instanceof escenas.Arena) {
                    final String[] stateParts = parts.clone();
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            ((escenas.Arena) gameController).applyServerState(stateParts);
                        }
                    });
                }
                break;

            case "EndGame":
                if (parts.length >= 2 && gameController instanceof escenas.Arena) {
                    final int winner = Integer.parseInt(parts[1]);
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            ((escenas.Arena) gameController).onGameEnded(winner);
                        }
                    });
                }
                break;

            case "Disconnect":
                if (gameController instanceof escenas.Arena) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            ((escenas.Arena) gameController).onServerDisconnected();
                        }
                    });
                }
                terminate();
                break;
        }
    }

    public void sendHeroDied(int heroVida, int intentosRestantes, float multVida, float multDanio, float multVelocidad, float multSalto) {
        sendMessage("HeroDied:" + heroVida + ":" + intentosRestantes + ":" + 
                   multVida + ":" + multDanio + ":" + multVelocidad + ":" + multSalto);
    }

    public void sendHeroUpgraded(float multVida, float multDanio, float multVelocidad, float multSalto, int numIntentos) {
        sendMessage("HeroUpgraded:" + multVida + ":" + multDanio + ":" + multVelocidad + ":" + multSalto+ ":"+ numIntentos);
    }

    public void sendMessage(String message) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServer, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendInput(int rol, int keycode) {
        System.out.println("Client: Sending input - Role: " + rol + ", Keycode: " + keycode);
        sendMessage("Input:" + rol + ":" + keycode);
    }

    public void connectToServer() {
        sendMessage("Connect");
    }

    public void terminate() {
        this.end = true;

        // Notificar al servidor antes de cerrar
        if (socket != null && !socket.isClosed()) {
            try {
                sendMessage("Disconnect");
                Thread.sleep(100); // Dar tiempo para que se envíe el mensaje
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Error sending disconnect message: " + e.getMessage());
            }
            socket.close();
        }

        this.interrupt();
        instance = null; // Limpiar la instancia
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
