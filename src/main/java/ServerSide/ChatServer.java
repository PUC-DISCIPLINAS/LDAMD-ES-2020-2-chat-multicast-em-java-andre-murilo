package ServerSide;

import domain.Models.Room;
import domain.Models.User;
import domain.Repositories.RoomRepository;
import network.ReceivePacketEvent;
import network.Session;
import network.packets.ClientPacket.CP_EnterRoomRequest;
import network.packets.ClientPacket.CP_LeaveRoomRequest;
import network.packets.ClientPacket.CP_LoginRequest;
import network.packets.ClientPacket.CP_SendChatMessage;
import network.packets.Packet;
import network.packets.PacketBase;
import network.packets.ServerPacket.SP_LoginResponse;
import network.packets.ServerPacket.SP_UserEnteredRoom;
import network.packets.ServerPacket.SP_UserLeaveRoom;
import network.packets.ServerPacket.SP_UserSentChatMessage;
import network.server.ServerConfig;
import network.server.TcpServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatServer
{
    private static ChatServer instance;
    private static final int DEFAULT_ROOM = 1;

    private TcpServer listener;
    public RoomRepository rooms;
    public Map<Session, User> users;

    public ChatServer()
    {
        instance = this;
        rooms = new RoomRepository();
        users = new HashMap();
    }

    public void run() throws InterruptedException {
        if(!initServer("127.0.0.1", 43002))
        {
            System.out.println("Falha ao iniciar o servidor...");
            System.exit(0);
        }

        System.out.println("Server is running...");

        // create default room
        Room room = new Room(DEFAULT_ROOM, "Canal Principal");
        room.setMaxUsers(100);

        rooms.add(room);

        // waiting for exit
        while(true)
            Thread.sleep(16);
    }


    private boolean initServer(String ip, int port)
    {
        ServerConfig config = new ServerConfig(ip, port);
        config.setMaxConnections(100);
        config.setCallbacks(ChatServer::OnClientConnected, ChatServer::OnClientDisconnected, ChatServer::OnClientSendPacket);

        listener = new TcpServer(config);
        try {

            return listener.runServer();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public void quitUserFromRoom(Session session)
    {
        User user = users.get(session);

        if(!user.isLogged() || !user.isInRoom())
            return;

        int current_room = user.getCurrentRoom();

        if(current_room == DEFAULT_ROOM)
        {
            Room room = rooms.getByID(DEFAULT_ROOM);
            if(room != null)
            {
                // send packet
                SP_UserLeaveRoom leave = new SP_UserLeaveRoom();
                leave.user_source = user;

                users.forEach((k, v) -> {
                    if (    v.isLogged() &&
                            v.isInRoom() &&
                            v.getCurrentRoom() == user.getCurrentRoom())

                        k.sendPacket(leave);
                });

                // remove
                user.setCurrentRoom(0);
                room.removeUser(user);
            }
        }
    }

    // callbacks
    public static Boolean OnClientConnected(Session client)
    {
        System.out.println("Client has Connected!");

        instance.users.put(client, User.createDefaultUser());
        return true;
    }

    public static Boolean OnClientDisconnected(Session client)
    {
        System.out.println("Client has Disconnected!");

        instance.quitUserFromRoom(client);
        instance.users.remove(client);
        return true;
    }

    public static Boolean OnClientSendPacket(ReceivePacketEvent packet)
    {
        Object obj = packet.getObj();
        Session session = packet.getSession();

        if(obj instanceof CP_LoginRequest)
        {
            procLoginRequest(session, (CP_LoginRequest)obj);
        }
        else if(obj instanceof CP_EnterRoomRequest)
        {
            procRoomEnterRequest(session, (CP_EnterRoomRequest)obj);
        }
        else if(obj instanceof CP_LeaveRoomRequest)
        {
            procRoomLeaveRequest(session, (CP_LeaveRoomRequest)obj);
        }
        else if(obj instanceof CP_SendChatMessage)
        {
            procSendChatMsg(session, (CP_SendChatMessage)obj);
        }
        else
        {
            System.out.println("[DEBUG] Client Packet not exist in this context.");
        }

        System.out.println("Client send packet");
        return true;
    }

    private static void procLoginRequest(Session session, CP_LoginRequest packet)
    {
        User user = ChatServer.instance.users.get(session);
        String username = packet.username;


        SP_LoginResponse login_response = new SP_LoginResponse();
        if(user.isLogged() || username.length() < 4)
        {
            login_response.setResponse_status(0);
        }
        else
        {
            user.setName(packet.username);
            login_response.setResponse_status(1);
        }

        session.sendPacket(login_response);
    }

    private static void procRoomEnterRequest(Session session, CP_EnterRoomRequest packet)
    {
        int room_target = packet.room_target_id;
        User user = instance.users.get(session);

        if(!user.isLogged() || user.isInRoom())
        {
            return;
        }

        if(room_target == 0)
        {
            // enter default
            Room default_room = instance.rooms.getByID(DEFAULT_ROOM);
            if(default_room != null)
            {
                // add user to room.
                user.setCurrentRoom(DEFAULT_ROOM);
                default_room.addUser(user);

                // send message to clients.
                SP_UserEnteredRoom response = new SP_UserEnteredRoom();
                response.user_source = user;

                instance.users.forEach((k, v) -> {
                    if (    v.isLogged() &&
                            v.isInRoom() &&
                            v.getCurrentRoom() == DEFAULT_ROOM)
                    {
                        k.sendPacket(response);
                    }
                });
            }
        }
    }

    private static void procRoomLeaveRequest(Session session, CP_LeaveRoomRequest packet)
    {
        User user = instance.users.get(session);

        if(!user.isLogged() || !user.isInRoom())
            return;

        instance.quitUserFromRoom(session);
    }

    private static void procSendChatMsg(Session session, CP_SendChatMessage packet)
    {
        User user = instance.users.get(session);

        if(!user.isLogged() || !user.isInRoom())
            return;

        SP_UserSentChatMessage chat_msg = new SP_UserSentChatMessage();
        chat_msg.user = user;
        chat_msg.message = packet.message;

        // send message to users in user room.
        instance.users.forEach((k, v) -> {
            if (    v.isLogged() &&
                    v.isInRoom() &&
                    v.getCurrentRoom() == user.getCurrentRoom() &&
                    v != user)
                k.sendPacket(chat_msg);
        });
    }
}
