package nure.ua.clothesstore.dao;

import nure.ua.clothesstore.entity.User;

import java.util.List;

public interface CRUDRepository<T> {
    long add(T entity);

    T update(T entity) throws DBException;

    void delete(long id) throws DBException;

    T findById(long id);

    List<T> findAll() throws DBException;

}
