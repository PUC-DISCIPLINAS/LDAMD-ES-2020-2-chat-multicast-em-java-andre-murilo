package network.packets.ServerPacket;

import domain.Models.User;

public class SP_UserLeaveRoom extends SP_Base
{
    public User user_source;

    public SP_UserLeaveRoom()
    {
        this.packet_id = 1002;
    }
}
