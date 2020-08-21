package network.packets.ClientPacket;

public class CP_LoginRequest extends CP_Base
{
    public String username;

    public CP_LoginRequest()
    {
        this.packet_id = 1;
    }
}
