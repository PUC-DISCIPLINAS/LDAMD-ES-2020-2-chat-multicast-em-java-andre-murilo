package network.packets;

import network.Session;

public class Packet
{
    private byte[] data;

    public Packet(byte[] data)
    {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}