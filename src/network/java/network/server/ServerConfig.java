package network.server;

import network.ReceivePacketEvent;

import java.util.function.Function;

public class ServerConfig
{
    // vars config
    private String ip;
    private int port;
    private int max_connections;

    // callbacks
    private Function<network.Session, Boolean> OnClientConnected;
    private Function<network.Session, Boolean> OnClientDisconnected;
    private Function<ReceivePacketEvent, Boolean> OnClientSendPacket;


    public ServerConfig(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.max_connections = 100;
    }

    public void setMaxConnections(int value) {
        this.max_connections = value;
    }

    public void setCallbacks(
            Function<network.Session, Boolean> OnClientConnected,
            Function<network.Session, Boolean> OnClientDisconnected,
            Function<ReceivePacketEvent, Boolean> OnClientSendPacket
    )
    {
        this.OnClientConnected = OnClientConnected;
        this.OnClientDisconnected = OnClientDisconnected;
        this.OnClientSendPacket = OnClientSendPacket;
    }

    public String getIP() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public int getMaxConnections() {
        return this.max_connections;
    }

    public Function<network.Session, Boolean> getOnClientConnectedCallback() {
        return this.OnClientConnected;
    }

    public Function<network.Session, Boolean> getOnClientDisconnectedCallback() {
        return this.OnClientDisconnected;
    }

    public Function<ReceivePacketEvent, Boolean> getOnClientSendPacketCallback() {
        return this.OnClientSendPacket;
    }
}
