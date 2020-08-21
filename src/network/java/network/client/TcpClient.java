package network.client;

import network.Session;
import network.packets.ClientPacket.CP_Base;
import network.packets.Packet;
import network.ReceivePacketEvent;
import network.packets.PacketBase;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class TcpClient
{
    private ClientConfig config;
    private Socket socket;
    private Session session;

    public TcpClient(ClientConfig config)
    {
        this.config = config;
        this.socket = new Socket();
    }

    public boolean connect()  {
        try {
            socket.connect(new InetSocketAddress(config.getIp(), config.getPort()), 5000);
            session = new Session(socket);
            receive();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Session getSession() {
        return this.session;
    }

    private void receive() {
        Thread t = new Thread(() -> {
            try {
                InputStream input = socket.getInputStream();

                DataInputStream inputStream = new DataInputStream(session.getInputStream());

                while(session.isConnected())
                {
                    int length = 0;
                    byte[] data;

                    try {
                        length = inputStream.readInt();
                        data = new byte[length];
                        inputStream.readFully(data, 0, length);

                        Packet packet = new Packet(data);
                        handlePacket(packet);

                        Thread.sleep(1);
                    }
                    catch (SocketException ex)
                    {

                    } catch (IOException | InterruptedException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                // disconnected
                config.getOnDisconnectedCallback().apply(session);


            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        t.start();
    }

    private void handlePacket(Packet packet) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(
                new ByteArrayInputStream(packet.getData()));

        Object obj = stream.readObject();

        if(obj instanceof PacketBase)
            config.getOnReceivePacketCallback().apply(new ReceivePacketEvent(session, packet, obj));
    }
}