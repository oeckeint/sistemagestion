package datos.entity;

public interface GenericEntity<T> {

    Long getId();

    // based on current data create new instance with new id
    T createNewInstance();

    // update current instance with provided data
    void update(T source);

}