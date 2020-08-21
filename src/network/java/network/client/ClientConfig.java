package network.client;

import network.Session;
import network.ReceivePacketEvent;

import java.util.function.Function;

public class ClientConfig
{
    private String ip;
    private int port;

    private Function<network.Session, Boolean> OnDisconnected;
    private Function<ReceivePacketEvent, Boolean> OnReceivePacket;

    public ClientConfig(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }

    public void setCallbacks(
            Function<network.Session, Boolean> OnDisconnected,
            Function<ReceivePacketEvent, Boolean> OnReceivePacket
    )
    {
        this.OnDisconnected = OnDisconnected;
        this.OnReceivePacket = OnReceivePacket;
    }

    public String getIp()
    {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public Function<Session, Boolean> getOnDisconnectedCallback() {
        return OnDisconnected;
    }

    public Function<ReceivePacketEvent, Boolean> getOnReceivePacketCallback() {
        return OnReceivePacket;
    }
}
