package network;

import network.Session;
import network.packets.Packet;

public class ReceivePacketEvent
{
    private Session session;
    private Packet packet;
    private Object obj;

    public ReceivePacketEvent(Session session, Packet packet, Object obj)
    {
        this.session = session;
        this.packet = packet;
        this.obj = obj;
    }

    public Session getSession() {
        return session;
    }

    public Packet getPacket() {
        return packet;
    }

    public Object getObj()
    {
        return obj;
    }
}
