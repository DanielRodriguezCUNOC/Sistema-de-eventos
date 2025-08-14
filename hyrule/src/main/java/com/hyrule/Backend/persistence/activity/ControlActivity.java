package com.hyrule.Backend.persistence.activity;

import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.persistence.Control;

public class ControlActivity extends Control<ActivityModel> {

    @Override
    public ActivityModel insert(ActivityModel entity) {
        return entity;
    }

    @Override
    public void update(ActivityModel entity) {
    }

    @Override
    public void delete(String key) {
    }

    @Override
    public ActivityModel findByKey(String key) {
        return null;
    }

    @Override
    public List<ActivityModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
