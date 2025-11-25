package red;

import java.io.IOException;
import java.net.*;

import com.badlogic.gdx.Gdx;

import Interfaces.GameController;

public class ClientThread extends Thread {

    private static ClientThread instance;

    private DatagramSocket socket;
    private int serverPort = 5555;
    private String ipServerStr = "255.255.255.255";
    private InetAddress ipServer;
    private boolean end = false;
    private GameController gameController;

    // Para manejar timeout de conexión
    private boolean connected = false;
    private boolean connectionAttempted = false;
    private static final int CONNECTION_TIMEOUT = 5000; // 5 segundos

    private ClientThread(GameController gameController) {
        try {
            this.gameController = gameController;
            ipServer = InetAddress.getByName(ipServerStr);
            socket = new DatagramSocket();
            socket.setSoTimeout(CONNECTION_TIMEOUT); // Timeout para receive
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

    @Override
    public void run() {
        // Esperar respuesta inicial de conexión
        long startTime = System.currentTimeMillis();

        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
                processMessage(packet);

                // Si recibimos un mensaje, la conexión fue exitosa
                if (!connected && connectionAttempted) {
                    connected = true;
                }

            } catch (SocketTimeoutException e) {
                // Timeout - verificar si aún estamos intentando conectar
                if (connectionAttempted && !connected) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    if (elapsed > CONNECTION_TIMEOUT) {
                        System.out.println("Connection timeout - server not responding");
                        notifyConnectionFailed();
                        break;
                    }
                }
                // Si ya estamos conectados, ignorar timeouts (el servidor enviará datos cuando los tenga)

            } catch (SocketException e) {
                if (!end) {
                    System.out.println("Connection lost - socket closed");
                    if (gameController instanceof escenas.Arena) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                ((escenas.Arena) gameController).onServerDisconnected();
                            }
                        });
                    }
                }
                break;
            } catch (IOException e) {
                if (!end) {
                    e.printStackTrace();
                }
            }
        } while(!end);
    }

    private void notifyConnectionFailed() {
        if (gameController instanceof escenas.Arena) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    ((escenas.Arena) gameController).onConnectionFailed();
                }
            });
        }
    }

    private void processMessage(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");

        System.out.println("Mensaje recibido: " + message);

        switch(parts[0]){
            case "AlreadyConnected":
                System.out.println("Ya estas conectado");
                connected = true;
                break;
            case "Connected":
                int assignedRole = Integer.parseInt(parts[1]);
                System.out.println("Conectado al servidor como rol: " + assignedRole);
                this.ipServer = packet.getAddress();
                connected = true;

                if (gameController instanceof escenas.Arena) {
                    ((escenas.Arena) gameController).setPlayerRole(assignedRole);
                }
                break;
            case "Full":
                System.out.println("Servidor lleno");
                connected = true;
                this.end = true;
                // TODO: Podrías agregar un callback específico para "servidor lleno"
                break;
            case "Start":
                this.gameController.startGame();
                break;
            case "ShowUpgradeMenu":
                System.out.println("DEBUG: Received ShowUpgradeMenu message");
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

            case "PlayerDisconnected":
                System.out.println("Other player disconnected");
                if (gameController instanceof escenas.Arena) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            ((escenas.Arena) gameController).onPlayerDisconnected();
                        }
                    });
                }
                break;
        }
    }

    public void sendHeroUpgraded(float multVida, float multDanio, float multVelocidad, float multSalto, int numIntentos) {
        sendMessage("HeroUpgraded:" + multVida + ":" + multDanio + ":" + multVelocidad + ":" + multSalto+ ":"+ numIntentos);
    }

    public void sendMessage(String message) {
        if (socket == null || socket.isClosed()) {
            return;
        }
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServer, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    public void sendInput(int rol, int keycode) {
        System.out.println("Client: Sending input - Role: " + rol + ", Keycode: " + keycode);
        sendMessage("Input:" + rol + ":" + keycode);
    }

    public void connectToServer() {
        connectionAttempted = true;
        sendMessage("Connect");
    }

    public void terminate() {
        this.end = true;

        if (socket != null && !socket.isClosed()) {
            try {
                sendMessage("Disconnect");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Error sending disconnect message: " + e.getMessage());
            }
            socket.close();
        }

        this.interrupt();
        instance = null;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean getConencted() {
        return this.connected;
    }
}