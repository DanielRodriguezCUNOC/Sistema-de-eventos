package com.hyrule.Backend.persistence.inscripcion;

import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.persistence.Control;

public class ControlRegistration extends Control<RegistrationModel> {

    @Override
    public RegistrationModel insert(RegistrationModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public void update(RegistrationModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public RegistrationModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    @Override
    public List<RegistrationModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
