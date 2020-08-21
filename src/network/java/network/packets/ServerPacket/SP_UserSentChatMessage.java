package network.packets.ServerPacket;

import domain.Models.User;

public class SP_UserSentChatMessage extends SP_Base
{
    public User user;
    public String message;

    public SP_UserSentChatMessage()
    {
        this.packet_id = 1003;
    }
}
