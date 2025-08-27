package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Localita;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InserisciLocalitaDAO implements GenericProcedureDAO<Localita> {
    @Override
    public Localita execute(Object... params) throws DAOException, SQLException {
        Localita localita= (Localita)  params[0];
        if (localita == null) {
            throw new DAOException("Località non può essere null");
        }
        else {
            try (Connection conn = ConnectionFactory.getConnection();
                 CallableStatement cs = conn.prepareCall("{call inserisciLocalita(?,?,?,?)}"))
            {
                cs.setString(1, localita.getNome());
                cs.setString(2, localita.getStato());
                cs.setString(3, localita.getProvincia());
                cs.setString(4, localita.getRegione());
                cs.executeQuery();


            } catch (SQLException e) {
//                if (e.getMessage().equals("45000")) {
//                    System.err.println("Messaggio del sistema: " + e.getMessage());
//                }
                throw new DAOException(e.getMessage(), e);
            }
        }
        return localita;


    }
}
