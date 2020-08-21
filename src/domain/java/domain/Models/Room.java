package domain.Models;

import java.util.ArrayList;

public class Room extends Base
{
    private int id;
    private String name;
    private ArrayList<User> users;
    private int maxUsers;

    public Room(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.users = new ArrayList();
        this.maxUsers = 20;
    }

    public boolean addUser(User user) {
        if(users.contains(user))
            return false;

        boolean is_added = users.add(user);
        if(is_added) {
            user.setCurrentRoom(this.id);
            return  true;
        }
        return false;

    }

    public boolean removeUser(User user) {
        return users.remove(user);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User[] getUsers() {
        return (User[])users.toArray();
    }
}
