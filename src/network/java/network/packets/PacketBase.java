package network.packets;

import java.io.Serializable;

public abstract class PacketBase implements Serializable
{
    // identificacao do pacote.
    protected int packet_id = 0;

    // possui criptografia?
    protected boolean is_encrypted = false;   // NAO IMPLEMENTADO

    // possui compactacao?
    protected boolean is_compressed = false;  // NAO IMPLEMENTADO

    public int getPacket_id() {
        return packet_id;
    }

    public boolean isEncrypted() {
        return is_encrypted;
    }

    public boolean isCompressed() {
        return is_compressed;
    }
}
