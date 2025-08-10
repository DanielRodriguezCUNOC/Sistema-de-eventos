package com.hyrule.Backend.persistence;

import java.sql.SQLException;
import java.util.List;

public abstract class Control<T> {
    public abstract T insert(T entity) throws SQLException;
    public abstract void update(T entity) throws SQLException;
    public abstract void delete(String key) throws SQLException;
    public abstract T findByKey(String key) throws SQLException;
    public abstract List<T> findAll() throws SQLException;
    
}
