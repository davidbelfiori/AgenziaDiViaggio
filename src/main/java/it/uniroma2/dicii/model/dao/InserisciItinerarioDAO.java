package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Itinerario;

import java.sql.SQLException;

public class InserisciItinerarioDAO implements GenericProcedureDAO<Itinerario> {

    @Override
    public Itinerario execute(Object... params) throws DAOException, SQLException {
        Itinerario itinerario = (Itinerario) params[0];
        if (itinerario == null) {
            throw new DAOException("Itinerario non può essere null");
        } else {
            try (var conn = ConnectionFactory.getConnection();
                 var cs = conn.prepareCall("{call agenziadiviaggi.creaItinerario(?,?,?,?)}")) {
                cs.setInt(1, itinerario.getDurata());
                cs.setFloat(2, itinerario.getCosto());
                cs.setString(3, itinerario.getNomeItinerario());
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.executeQuery();
                itinerario.setIdItinerario(cs.getInt(4));
                return itinerario;
            } catch (SQLException e) {
                if (e.getMessage().equals("45000")) {
                    System.err.println("Messaggio del sistema: " + e.getMessage());
                }
                throw new DAOException(e.getMessage(), e);
            }
        }

    }
}
