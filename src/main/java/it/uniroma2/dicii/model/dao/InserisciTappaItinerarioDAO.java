package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Tappa;

import java.sql.SQLException;

public class InserisciTappaItinerarioDAO implements GenericProcedureDAO<Tappa> {
    @Override
    public Tappa execute(Object... params) throws DAOException, SQLException {

        Tappa tappaInserita = (Tappa) params[0];
        if (tappaInserita == null) {
            throw new DAOException("Tappa non può essere null");
        } else {
            try (var conn = ConnectionFactory.getConnection();
                 var cs = conn.prepareCall("{call agenziadiviaggi.aggiungiTappaItinerario(?,?,?,?,?)}")) {
                cs.setInt(1, tappaInserita.getIdItinerario());
                cs.setString(2, tappaInserita.getNomeLocalita());
                cs.setString(3, tappaInserita.getStato());
                cs.setInt(4, tappaInserita.getOrdine());
                cs.setInt(5, tappaInserita.getGiorni());
                cs.executeQuery();
                return tappaInserita;
            } catch (SQLException e) {
//                if (e.getMessage().equals("45000")) {
//                    System.err.println("Messaggio del sistema: " + e.getMessage());
//                }
                throw new DAOException(e.getMessage(), e);
            }
        }

    }
}
