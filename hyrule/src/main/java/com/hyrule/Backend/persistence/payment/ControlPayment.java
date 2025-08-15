package com.hyrule.Backend.persistence.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.model.payment.PaymentType;
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

    public List<PaymentModel> findPaymentsByCorreoOrEvento(String filter) throws SQLException {
        List<PaymentModel> result = new ArrayList<>();
        String sql = "SELECT correo, codigo_evento, metodo_pago, monto FROM pago " +
                "WHERE LOWER(correo) LIKE ? OR LOWER(codigo_evento) LIKE ?";
        try (Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeFilter = "%" + filter.toLowerCase() + "%";
            pstmt.setString(1, likeFilter);
            pstmt.setString(2, likeFilter);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentModel payment = new PaymentModel();
                    payment.setCorreo(rs.getString("correo_participante"));
                    payment.setCodigoEvento(rs.getString("codigo_evento"));
                    payment.setTipoPago(PaymentType.valueOf(rs.getString("metodo_pago").toUpperCase()));
                    payment.setMonto(rs.getBigDecimal("monto"));
                    result.add(payment);
                }
            }

        }
        return result;
    }
}
