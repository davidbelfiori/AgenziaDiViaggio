package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class InserisciPrenotazioneDAO implements GenericProcedureDAO {

    @Override
    public Object execute(Object... params) throws DAOException, SQLException {
        if (params.length < 2)
            throw new DAOException("Parametri insufficienti per inserire la prenotazione");
        int numeroPartecipanti = (int) params[0];
        int idViaggio = (int) params[1];
        String codiceDisdetta = null;

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call inserisciPrenotazione(?, ?, ?)}")) {
            cs.setInt(1, numeroPartecipanti);
            cs.setInt(2, idViaggio);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.executeQuery();
            codiceDisdetta=cs.getString(3);
            return  codiceDisdetta;

        } catch (SQLException e) {
//            if(e.getMessage().equals("45000")){
//                System.err.println("Messaggio del sistema"+e.getMessage());
//            }
            throw new DAOException(e.getMessage(), e);
        }
    }

    }

