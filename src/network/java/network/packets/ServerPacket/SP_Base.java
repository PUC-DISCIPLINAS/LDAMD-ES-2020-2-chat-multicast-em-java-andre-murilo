package network.packets.ServerPacket;

import network.packets.PacketBase;

public abstract class SP_Base extends PacketBase
{
    protected int response_status;

    public int getResponse_status() {
        return response_status;
    }

    public void setResponse_status(int response_status) {
        this.response_status = response_status;
    }
}
