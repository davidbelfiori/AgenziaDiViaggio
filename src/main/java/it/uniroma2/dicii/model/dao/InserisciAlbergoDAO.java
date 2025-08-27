package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Albergo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InserisciAlbergoDAO implements GenericProcedureDAO<Albergo> {


    @Override
    public Albergo execute(Object... params) throws DAOException, SQLException {

            Albergo albergo = (Albergo) params[0];
            if (albergo == null) {
                throw new DAOException("Albergo non può essere null");
            }else {
                try (Connection conn = ConnectionFactory.getConnection();
                     CallableStatement cs = conn.prepareCall("{call agenziadiviaggi.inserisciAlbergo(?,?,?,?,?,?,?,?,?,?,?)}")
                ) {
                    cs.setString(1, albergo.getNome());
                    cs.setInt(2, albergo.getCapienza());
                    cs.setFloat(3, albergo.getCostoPN());
                    cs.setString(4, albergo.getReferente());
                    cs.setString(5, albergo.getVia());
                    cs.setString(6, albergo.getNumero());
                    cs.setString(7, albergo.getCap());
                    cs.setString(8, albergo.getEmail());
                    cs.setString(9, albergo.getFax());
                    cs.setString(10, albergo.getNomeLocalita());
                    cs.setString(11, albergo.getStato());
                    cs.executeQuery();
                }catch (SQLException e) {
//                    if (e.getMessage().equals("45000")) {
//                        System.err.println("Messaggio del sistema: " + e.getMessage());
//                    }
                    throw new DAOException(e.getMessage(), e);

                }
            }


        return albergo;
    }
}
