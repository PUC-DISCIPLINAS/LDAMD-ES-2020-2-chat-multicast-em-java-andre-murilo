package network;

import network.packets.PacketBase;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Session
{
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    public Session(Socket sock) throws IOException
    {
        this.socket = sock;
        this.input = sock.getInputStream();
        this.output = sock.getOutputStream();

        // set Socket NO DELAY
        this.socket.setTcpNoDelay(true);
    }

    public InputStream getInputStream() {
        return this.input;
    }
    public OutputStream getOutputStream() {
        return this.output;
    }

    public boolean sendPacket(PacketBase packet)
    {
        if(!isConnected())
            return false;

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(stream);
            os.writeObject(packet);

            byte[] data = stream.toByteArray();
            os.close();

            DataOutputStream out = new DataOutputStream(output);
            out.writeInt(data.length);
            output.write(data);

            //output.flush();

            //os.close();

            return true;
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public boolean disconnect() {
        try {
            socket.close();
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

}
