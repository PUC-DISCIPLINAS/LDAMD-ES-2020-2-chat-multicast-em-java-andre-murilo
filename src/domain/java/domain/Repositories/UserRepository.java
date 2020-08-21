package domain.Repositories;
import domain.Models.User;
import java.util.ArrayList;

public class UserRepository implements IRepository<User>
{
    private ArrayList<User> users;

    public UserRepository() {
        users = new ArrayList();
    }

    @Override
    public boolean add(User obj) {
        if(users.contains(obj))
            return false;
        return users.add(obj);
    }

    @Override
    public boolean remove(User obj) {
        return users.remove(obj);
    }

    @Override
    public User getByID(int id) {
        return users.stream().filter(u -> u.getId() == id).findAny().orElse(null);
    }

    @Override
    public User[] getAll() {
        return (User[])users.toArray();
    }

    public User getUserByName(String name) {
        return users.stream().filter(u -> u.getName().equals(name)).findAny().orElse(null);
    }
}
