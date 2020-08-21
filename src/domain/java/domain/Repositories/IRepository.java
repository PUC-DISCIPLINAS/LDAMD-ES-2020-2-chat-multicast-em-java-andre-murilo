package domain.Repositories;

public interface IRepository<T>
{
    boolean add(T obj);
    boolean remove(T obj);
    T getByID(int id);
    T[] getAll();
}
