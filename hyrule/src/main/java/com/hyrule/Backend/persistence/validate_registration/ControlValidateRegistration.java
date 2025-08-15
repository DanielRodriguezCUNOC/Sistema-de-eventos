package com.hyrule.Backend.persistence.validate_registration;

import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;
import com.hyrule.Backend.persistence.Control;

public class ControlValidateRegistration extends Control<ValidateRegistrationModel> {

    @Override
    public ValidateRegistrationModel insert(ValidateRegistrationModel entity) throws SQLException {
        return entity;
    }

    @Override
    public void update(ValidateRegistrationModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public ValidateRegistrationModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    @Override
    public List<ValidateRegistrationModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
