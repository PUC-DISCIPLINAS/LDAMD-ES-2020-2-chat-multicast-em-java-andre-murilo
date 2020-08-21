package network.packets.ClientPacket;

public class CP_SendChatMessage extends CP_Base
{
    public String message;
    public CP_SendChatMessage()
    {
        this.packet_id = 4;
    }
}
