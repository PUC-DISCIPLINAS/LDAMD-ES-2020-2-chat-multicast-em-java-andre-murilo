package network.packets.ServerPacket;

import domain.Models.User;

public class SP_UserEnteredRoom extends SP_Base
{
    public User user_source;

    public SP_UserEnteredRoom()
    {
        this.packet_id = 1001;
    }
}
