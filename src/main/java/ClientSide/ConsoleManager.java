package ClientSide;

import domain.Models.Room;

import java.io.InputStream;
import java.util.Scanner;

public class ConsoleManager
{
    Scanner scanner;
    ChatClient client;

    public ConsoleManager(ChatClient client)
    {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public String enterUser()
    {
        System.out.println("------ AUTENTICACAO ------\n");

        System.out.println("Digite seu nome de usuario: ");
        return scanner.nextLine();
    }

    public String nextMessage()
    {
        return scanner.nextLine();
    }
}
