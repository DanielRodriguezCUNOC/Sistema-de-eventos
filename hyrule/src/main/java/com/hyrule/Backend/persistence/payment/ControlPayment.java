package com.hyrule.Backend.persistence.payment;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.model.payment.PaymentType;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de pagos.
 * Gestiona validaciones, inserción y consulta de pagos de eventos.
 */
public class ControlPayment extends Control<PaymentModel> {

    /**
     * Inserta un nuevo pago en la base de datos.
     * 
     * @param entity el pago a insertar
     * @param conn   conexión a la base de datos
     * @return el pago insertado
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public PaymentModel insert(PaymentModel entity, Connection conn) throws SQLException {

        // *Creamos el sql */
        String sql = "INSERT INTO pago (correo_participante, codigo_evento, metodo_pago, monto) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            try {
                pstmt.setString(1, entity.getCorreo());
                pstmt.setString(2, entity.getCodigoEvento());
                pstmt.setString(3, entity.getTipoPago().name());
                pstmt.setBigDecimal(4, entity.getMonto());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    throw new SQLException("No se pudo insertar el pago, no se afectaron filas.");
                }
                conn.commit();
                return entity;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Actualiza un pago existente.
     * 
     * @param entity el pago con datos actualizados
     * @param conn   conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(PaymentModel entity, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina un pago por clave.
     * 
     * @param key  la clave del pago a eliminar
     * @param conn conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca un pago por clave.
     * 
     * @param key  la clave de búsqueda
     * @param conn conexión a la base de datos
     * @return el pago encontrado o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public PaymentModel findByKey(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todos los pagos registrados.
     * 
     * @param conn conexión a la base de datos
     * @return lista de todos los pagos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<PaymentModel> findAll(Connection conn) throws SQLException {

        List<PaymentModel> result = new ArrayList<>();
        String sql = "SELECT correo_participante, codigo_evento, metodo_pago, monto FROM pago";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PaymentModel payment = new PaymentModel();
                payment.setCorreo(rs.getString("correo_participante"));
                payment.setCodigoEvento(rs.getString("codigo_evento"));
                payment.setTipoPago(PaymentType.valueOf(rs.getString("metodo_pago").toUpperCase()));
                payment.setMonto(rs.getBigDecimal("monto"));
                result.add(payment);
            }
        }
        return result;
    }

    /**
     * Valida un pago verificando duplicados y montos correctos.
     * 
     * @param payment el pago a validar
     * @param conn    la conexión a la base de datos
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validatePayment(PaymentModel payment, Connection conn) {

        // *Verificar si el pago ya existe */
        if (existsPayment(payment, conn)) {
            return "Ya existe un pago registrado para este participante y evento.";
        }

        if (!validatePaymentAmount(payment, conn)) {
            return "El monto del pago no coincide con el costo del evento.";

        }

        return "Ok";
    }

    /**
     * Valida que el monto del pago coincida con el costo del evento.
     * 
     * @param payment el pago a validar
     * @param conn    la conexión a la base de datos
     * @return true si el monto es correcto
     */
    private boolean validatePaymentAmount(PaymentModel payment, Connection conn) {

        String sql = "SELECT costo FROM evento WHERE codigo_evento = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payment.getCodigoEvento());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal costo = rs.getBigDecimal("costo");
                    return costo != null && costo.compareTo(payment.getMonto()) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si ya existe un pago para el participante y evento.
     * 
     * @param payment el pago a verificar
     * @param conn    la conexión a la base de datos
     * @return true si ya existe, false si no
     */
    private boolean existsPayment(PaymentModel payment, Connection conn) {

        String sql = "SELECT COUNT(*) FROM pago WHERE correo_participante = ? AND codigo_evento = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payment.getCorreo());
            pstmt.setString(2, payment.getCodigoEvento());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca pagos por correo o código de evento que contengan el filtro.
     * 
     * @param filter texto a buscar en correo o código de evento
     * @param conn   conexión a la base de datos
     * @return lista de pagos que coinciden con el filtro
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<PaymentModel> findPaymentsByCorreoOrEvento(String filter, Connection conn) throws SQLException {

        List<PaymentModel> result = new ArrayList<>();
        String sql = "SELECT correo_participante, codigo_evento, metodo_pago, monto FROM pago " +
                "WHERE LOWER(correo_participante) LIKE ? OR LOWER(codigo_evento) LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
