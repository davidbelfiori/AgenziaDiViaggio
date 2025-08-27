package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Albergo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlberghiPerLocalitaDAO implements GenericProcedureDAO<List<Albergo>> {

    public List<Albergo> execute(Object... params) throws DAOException {
        List<Albergo> alberghi = new ArrayList<>();
        int idViaggio = (Integer) params[0];
        String nomeLocalita = (String) params[1];
        String stato = (String) params[2];

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call visualizzaAlberghiPerLocalita(?, ?, ?)}")) {
            cs.setInt(1, idViaggio);
            cs.setString(2, nomeLocalita);
            cs.setString(3, stato);
            ResultSet rs = cs.executeQuery();
                while (rs.next()) {
                    Albergo albergo = new Albergo(
                            rs.getInt("CodiceAlbergo"),
                            rs.getString("Nome"),
                            rs.getInt("Capienza"),
                            rs.getFloat("CostoPN"),
                            rs.getString("Referente"),
                            rs.getString("Via"),
                            rs.getString("Numero"),
                            rs.getString("Cap"),
                            rs.getString("Email"),
                            rs.getString("Fax"),
                            rs.getString("NomeLocalita"),
                            rs.getString("Stato")
                    );
                    alberghi.add(albergo);
                }

        }catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
        return alberghi;
    }
}
