package network.server;

import network.ReceivePacketEvent;
import network.Session;
import network.packets.ClientPacket.CP_Base;
import network.packets.Packet;
import network.packets.PacketBase;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;

public class TcpServer
{
    private final ServerConfig config;
    private ServerSocket listener;
    private boolean running;

    ArrayList<Session> clients;

    public TcpServer(ServerConfig config) {
        this.config = config;
        this.clients = new ArrayList<>();
    }

    public Session[] getSessions() {
        return (Session[])clients.toArray();
    }

    // Inicia o servidor.
    public boolean runServer() throws IOException {
        listener = new ServerSocket(config.getPort(), 10);
        running = true;

        accept();
        return true;
    }

    // Pausa ou encerra todas as conexoes ativas.
    public boolean stopServer(boolean close_connections) {
        if(!running)
            return false;

        if(close_connections) {
            clients.forEach((session) -> { session.disconnect(); });
            clients.clear();
        }

        running = false;
        return true;
    }

    // Aceita as conexoes e cria uma thread unica para cada client.
    private void accept() {
        Thread thread_accept = new Thread(() -> {
            while(running)
            {
                try {
                    Socket client_sock = listener.accept();
                    Session session = new Session(client_sock);
                    clients.add(session);

                    config.getOnClientConnectedCallback().apply(session);
                    receive(session);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread_accept.start();
    }

    // Recebendo dados do cliente.
    private void receive(Session session) {
        Thread thread_receive = new Thread(() -> {
            Scanner scanner = new Scanner(session.getInputStream());

            DataInputStream inputStream = new DataInputStream(session.getInputStream());

            while (session.isConnected()) {
                int length = 0;
                byte[] data;

                try {
                    length = inputStream.readInt();

                    data = new byte[length];
                    inputStream.readFully(data, 0, length);

                    Packet packet = new Packet(data);
                    handlePacket(session, packet);

                    Thread.sleep(16);

                } catch (SocketException ex) {
                    break;
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    break;
                }
            }

            // A conexao foi encerrada.
            config.getOnClientDisconnectedCallback().apply(session);

            if(clients.contains(session))
                clients.remove(session);
        });

        thread_receive.start();
    }

    // Faz a interpretacao da mensagem e pega o tipo do pacote + argumentos.
    private void handlePacket(Session session, Packet packet) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(
                new ByteArrayInputStream(packet.getData()));

        Object obj = stream.readObject();

        if(obj instanceof PacketBase)
        {
            config.getOnClientSendPacketCallback().apply(new ReceivePacketEvent(session, packet, obj));
        }
    }
}