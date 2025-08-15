package com.hyrule.Backend.persistence.payment;

import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.persistence.Control;

public class ControlPayment extends Control<PaymentModel> {

    @Override
    public PaymentModel insert(PaymentModel entity) throws SQLException {
        return entity;
    }

    @Override
    public void update(PaymentModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public PaymentModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    @Override
    public List<PaymentModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}
