package domain.Models;

public class User extends Base
{
    private String name;
    private int currentRoom;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isLogged() {
        return !name.isEmpty();
    }

    public boolean isInRoom() {
        return currentRoom > 0;
    }

    public static User createDefaultUser()
    {
        return new User(0, "");
    }
}
