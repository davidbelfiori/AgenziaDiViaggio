package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;

import java.sql.SQLException;
import java.sql.Types;

public class GeneraReportViaggioDAO implements GenericProcedureDAO{
    @Override
    public Object execute(Object... params) throws DAOException, SQLException {
        int idViaggio = (Integer) params[0];

        try (var conn = ConnectionFactory.getConnection();
             var cs = conn.prepareCall("{call agenziadiviaggi.generaReportViaggio(?,?)}")) {
            cs.setInt(1, idViaggio);
            cs.registerOutParameter(2, Types.LONGVARCHAR);
            cs.executeQuery();
            String report = cs.getString(2);
            System.out.println(report);
        } catch (SQLException e) {
            if (e.getMessage().equals("45000")) {
                System.err.println("Messaggio del sistema: " + e.getMessage());
            }
            throw new DAOException(e.getMessage(), e);
        }
        return null;
    }
}
