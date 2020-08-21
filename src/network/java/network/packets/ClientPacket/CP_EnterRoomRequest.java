package network.packets.ClientPacket;

public class CP_EnterRoomRequest extends CP_Base
{
    public int room_target_id;

    public CP_EnterRoomRequest()
    {
        this.packet_id = 2;
        this.room_target_id = 0; // ANY ROOM
    }
}
