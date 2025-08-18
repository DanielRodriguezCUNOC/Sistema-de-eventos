package com.hyrule.Backend.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase abstracta base para controladores de persistencia CRUD.
 * Define las operaciones básicas para el manejo de entidades en base de datos.
 * 
 * @param <T> el tipo de entidad que maneja el controlador
 */
public abstract class Control<T> {

    /**
     * Inserta una nueva entidad en la base de datos.
     * 
     * @param entity la entidad a insertar
     * @return la entidad insertada
     * @throws SQLException si ocurre un error en la base de datos
     */
    public abstract T insert(T entity, Connection conn) throws SQLException;

    /**
     * Actualiza una entidad existente en la base de datos.
     * 
     * @param entity la entidad con datos actualizados
     * @throws SQLException si ocurre un error en la base de datos
     */
    public abstract void update(T entity, Connection conn) throws SQLException;

    /**
     * Elimina una entidad por su clave.
     * 
     * @param key la clave de la entidad a eliminar
     * @throws SQLException si ocurre un error en la base de datos
     */
    public abstract void delete(String key, Connection conn) throws SQLException;

    /**
     * Busca una entidad por su clave.
     * 
     * @param key la clave de búsqueda
     * @return la entidad encontrada o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    public abstract T findByKey(String key, Connection conn) throws SQLException;

    /**
     * Obtiene todas las entidades almacenadas.
     * 
     * @return lista de todas las entidades
     * @throws SQLException si ocurre un error en la base de datos
     */
    public abstract List<T> findAll(Connection conn) throws SQLException;

}
