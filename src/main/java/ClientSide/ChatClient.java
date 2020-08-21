package ClientSide;

import domain.Models.Room;
import domain.Models.User;
import network.ReceivePacketEvent;
import network.Session;
import network.client.ClientConfig;
import network.client.TcpClient;
import network.packets.ClientPacket.CP_EnterRoomRequest;
import network.packets.ClientPacket.CP_LoginRequest;
import network.packets.ClientPacket.CP_SendChatMessage;
import network.packets.ServerPacket.SP_LoginResponse;
import network.packets.ServerPacket.SP_UserEnteredRoom;
import network.packets.ServerPacket.SP_UserLeaveRoom;
import network.packets.ServerPacket.SP_UserSentChatMessage;

import java.util.ArrayList;

public class ChatClient
{
    private static ChatClient instance;

    private TcpClient tcp_client;
    private Session session;

    private Boolean in_room;
    private ConsoleManager console;

    public ChatClient()
    {
        instance = this;
        console = new ConsoleManager(this);
        in_room = false;
    }

    public boolean isInRoom() {
        return in_room;
    }

    public void run() throws InterruptedException {

        // try to connect server
        if(!initializeNetwork("127.0.0.1", 43002))
        {
            System.out.println("Falha ao se conectar...");
            System.exit(0);
        }

        // get current session and do login
        session = tcp_client.getSession();
        System.out.println("Conectado com sucesso!");
        System.out.println("Dica: Digite /exit para sair de uma sala.");

        String user = console.enterUser();

        // send user request packet
        CP_LoginRequest loginRequest = new CP_LoginRequest();
        loginRequest.username = user;
        session.sendPacket(loginRequest);


        // waiting for exit current process.
        while (true)
        {
            if(isInRoom())
            {
                String message = console.nextMessage();

                // comandos
                if(message.equals("/exit")) {
                    System.out.println("Saindo da sala de bate papo...");
                    in_room = false;

                    session.disconnect();

                    System.exit(0);
                }

                // send packet
                CP_SendChatMessage packet = new CP_SendChatMessage();
                packet.message = message;

                session.sendPacket(packet);
            }

            Thread.sleep(16);
        }
    }

    private Boolean initializeNetwork(String ip, int port)
    {
        ClientConfig config = new ClientConfig(ip, port);
        config.setCallbacks(ChatClient::OnDisconnected, ChatClient::OnReceivePacket);

        tcp_client = new TcpClient(config);

        if(!tcp_client.connect())
            return false;

        return true;
    }


    private static Boolean OnDisconnected(Session session)
    {
        System.out.println("[ERROR] You have been disconnected from server.");
        return true;
    }


    /* ----------------- RECEIVE ----------------- */
    private static Boolean OnReceivePacket(ReceivePacketEvent packet)
    {
        Session session = packet.getSession();  // current session
        Object packet_obj = packet.getObj();    // raw object

        if(packet_obj instanceof SP_LoginResponse)
        {
            processLoginResponse(packet.getSession(), (SP_LoginResponse)packet.getObj());
        }
        else if(packet_obj instanceof SP_UserEnteredRoom)
        {
            processUserEnteredRoom(packet.getSession(), (SP_UserEnteredRoom) packet.getObj());
        }
        else if(packet_obj instanceof SP_UserLeaveRoom)
        {
            processUserLeaveRoom(packet.getSession(), (SP_UserLeaveRoom)packet.getObj());
        }
        else if(packet_obj instanceof SP_UserSentChatMessage)
        {
            processUserSentMessage(packet.getSession(), (SP_UserSentChatMessage)packet.getObj());
        }
        else
        {
            System.out.println("[DEBUG] Packet received not exists in this context.");
        }

        return true;
    }


    private static void processLoginResponse(Session session, SP_LoginResponse login_response)
    {
        int status = login_response.getResponse_status();

        // sucesso.
        if(status == 1)
        {
            System.out.println("[SERVER] Logado com sucesso.");

            // Send Enter Room request.
            CP_EnterRoomRequest enter_room = new CP_EnterRoomRequest();
            ChatClient.instance.session.sendPacket(enter_room);
        }
        // falha ao realizar login.
        else if(status == 0)
        {
            System.out.println("[SERVER] Falha ao realizar login. Tente novamente.");
        }
    }

    private static void processUserEnteredRoom(Session session, SP_UserEnteredRoom packet)
    {
        System.out.printf("[SERVER] O usuario %s entrou na sala.\n", packet.user_source.getName());
        instance.in_room = true;
    }

    private static void processUserLeaveRoom(Session session, SP_UserLeaveRoom packet)
    {
        System.out.printf("[SERVER] O usuario %s saiu da sala.\n", packet.user_source.getName());
    }

    private static void processUserSentMessage(Session session, SP_UserSentChatMessage packet)
    {
        System.out.printf("%s: %s\n", packet.user.getName(), packet.message);
    }
}
