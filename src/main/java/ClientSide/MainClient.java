package ClientSide;

import network.Session;
import network.client.ClientConfig;
import network.client.TcpClient;
import network.ReceivePacketEvent;

import javax.swing.*;

class MainClient
{
    public static void main(String[] args)
    {
        try {
            new ChatClient().run();
        }
        catch (Exception ex)
        {

        }
    }
}
