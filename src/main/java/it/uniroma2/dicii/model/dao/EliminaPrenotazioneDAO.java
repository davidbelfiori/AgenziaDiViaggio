package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Prenotazione;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class EliminaPrenotazioneDAO implements GenericProcedureDAO<Void> {


    @Override
    public Void execute(Object... params) throws DAOException, SQLException {
        if (params.length < 1)
            throw new DAOException("Codice disdetta mancante");

        String codiceDisdetta = (String) params[0];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call agenziadiviaggi.cancellazionePrenotazione(?)}")) {
            cs.setString(1, codiceDisdetta);
            cs.executeQuery();
        } catch (SQLException e) {
//            if (e.getMessage().equals("45000")) {
//                System.err.println(e.getMessage());
//            }
            throw new DAOException(e.getMessage(), e);
        }
        return null;
    }

}
