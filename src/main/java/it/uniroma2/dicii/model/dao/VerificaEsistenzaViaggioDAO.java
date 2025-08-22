package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Viaggio;

import java.sql.CallableStatement;
import java.sql.Connection;

public class VerificaEsistenzaViaggioDAO implements GenericProcedureDAO {

    @Override
    public Viaggio execute(Object... params)  throws DAOException {
        if (params.length < 1 || !(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("IDViaggio richiesto");
        }
        int idViaggio = (Integer) params[0];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call verificaEsistenzaViaggio(?, ?, ?)}")) {

            cs.setInt(1, idViaggio);
            cs.registerOutParameter(2, java.sql.Types.BOOLEAN); // esiste
            cs.registerOutParameter(3, java.sql.Types.VARCHAR); // Stato

            cs.execute();

            boolean esiste = cs.getBoolean(2);
            String stato = cs.getString(3);

            if (!esiste) {
                throw new DAOException("Viaggio non trovato");
            } else if (stato.equals("PROGRAMMATO")) {
                throw new DAOException("Viaggio programmato, non è possibile effettuare operazioni su di esso");
            }

            Viaggio v = new Viaggio();
            v.setIdViaggio(idViaggio);
            v.setStato(stato);
            return v;



        } catch (java.sql.SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
}
