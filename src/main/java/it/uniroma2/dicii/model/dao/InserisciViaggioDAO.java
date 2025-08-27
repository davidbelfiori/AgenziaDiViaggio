package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Viaggio;

import java.sql.*;

public class InserisciViaggioDAO implements GenericProcedureDAO<Viaggio> {


    @Override
    public Viaggio execute(Object... params) throws DAOException, SQLException {

        Viaggio viaggioInserito = (Viaggio) params[0];
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call creaViaggio(?,?,?,?)}")) {
            cs.setDate(1, (Date) viaggioInserito.getDataPartenza());
            cs.setInt(2, viaggioInserito.getIdItinerario());
            cs.registerOutParameter(3, java.sql.Types.INTEGER);
            cs.registerOutParameter(4, Types.DATE);
            cs.executeQuery();
            viaggioInserito.setIdViaggio(cs.getInt(3));
            viaggioInserito.setDataRientro(cs.getDate(4));
            return viaggioInserito;
        } catch (SQLException e) {
//            if (e.getMessage().equals("45000")) {
//                System.err.println("Messaggio del sistema: " + e.getMessage());
//            }
            throw new DAOException(e.getMessage(), e);
        }

    }
}
