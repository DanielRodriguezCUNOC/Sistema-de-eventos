package com.hyrule.Backend.persistence.participant;

import java.util.List;
import java.sql.SQLException;

import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.persistence.Control;

public class ControlParticipant extends Control<ParticipantModel> {

    // * Implementación de los métodos abstractos de Control */

    @Override
    public ParticipantModel insert(ParticipantModel entity) {

        return entity;
    }

    @Override
    public void update(ParticipantModel entity) {
    }

    @Override
    public void delete(String key) {
    }

    @Override
    public ParticipantModel findByKey(String key) {
        return null;
    }

    @Override
    public List<ParticipantModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
