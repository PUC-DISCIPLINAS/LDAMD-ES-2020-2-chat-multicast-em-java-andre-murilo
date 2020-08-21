package ServerSide;

import network.Session;
import network.ReceivePacketEvent;
import network.packets.ClientPacket.CP_LoginRequest;
import network.packets.ServerPacket.SP_LoginResponse;
import network.server.ServerConfig;
import network.server.TcpServer;

import java.io.IOException;

class MainServer
{

    public static void main(String[] args) throws IOException, InterruptedException
    {
        new ChatServer().run();
    }
}
