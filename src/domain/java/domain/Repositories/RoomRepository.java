package domain.Repositories;

import domain.Models.Room;

import java.util.ArrayList;

public class RoomRepository implements IRepository<Room>
{
    private ArrayList<Room> rooms;

    public RoomRepository() {
        rooms = new ArrayList<>();
    }

    @Override
    public boolean add(Room obj) {
        if(rooms.contains(obj))
            return false;
        return rooms.add(obj);
    }

    @Override
    public boolean remove(Room obj) {
        return rooms.remove(obj);
    }

    @Override
    public Room getByID(int id) {
        return rooms.stream().filter(r-> r.getId() == id).findAny().orElse(null);
    }

    @Override
    public Room[] getAll() {
        return (Room[])rooms.toArray();
    }

    public Room getRoomByName(String name) {
        return rooms.stream().filter(r -> r.getName().equals(name)).findAny().orElse(null);
    }
}