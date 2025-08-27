package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;

import java.sql.SQLException;

public class AssociaPernottamentoDAO implements GenericProcedureDAO{
    @Override
    public Object execute(Object... params) throws DAOException, SQLException {
        int idViaggio = (Integer) params[0];
        int idAlbergo = (Integer) params[1];

        try (var conn = ConnectionFactory.getConnection();
             var cs = conn.prepareCall("{call agenziadiviaggi.associaPernottamento(?,?)}")) {
            cs.setInt(1, idViaggio);
            cs.setInt(2, idAlbergo);
            cs.executeQuery();
        } catch (SQLException e) {
//            if (e.getMessage().equals("45000")) {
//                System.err.println("Messaggio del sistema: " + e.getMessage());
//            }
            throw new DAOException(e.getMessage(), e);
        }
        return null;
    }
}
