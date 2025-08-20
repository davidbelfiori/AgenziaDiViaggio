package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Autobus;

import java.sql.SQLException;

public class InserisciAutobusDAO implements GenericProcedureDAO<Autobus> {
    @Override
    public Autobus execute(Object... params) throws DAOException, SQLException {
        Autobus autobus = (Autobus) params[0];
        if (autobus == null) {
            throw new DAOException("Autobus non può essere null");
        } else {
            try (var conn = ConnectionFactory.getConnection();
                 var cs = conn.prepareCall("{call inserisciAutobus(?,?,?)}")) {
                cs.setString(1, autobus.getTarga());
                cs.setInt(2, autobus.getCapienza());
                cs.setFloat(3, autobus.getCostoForfettario());
                cs.executeQuery();
            } catch (SQLException e) {
                if (e.getMessage().equals("45000")) {
                    System.err.println("Messaggio del sistema: " + e.getMessage());
                }
                throw new DAOException(e.getMessage(), e);
            }
        }
        return autobus;
    }
}
