package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Autobus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutobusDisponibiliDAO implements GenericProcedureDAO<List<Autobus>> {


    @Override
    public List<Autobus> execute(Object... params) throws DAOException, SQLException {
        int idViaggio = (Integer) params[0];

        List<Autobus> autobusDisponibili = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs= conn.prepareCall("{call visualizzaAutobusDisponibili(?)}")

        ){
            cs.setInt(1,idViaggio);
           ResultSet rs= cs.executeQuery();
            if (rs == null || !rs.next()) {
                throw new DAOException("Nessun autobus disponibile per il viaggio con ID: " + idViaggio);
            }
            while (rs.next()){
                Autobus autobus = new Autobus();
                autobus.setTarga(rs.getString("Targa"));
                autobus.setCostoForfettario(rs.getFloat("CostoForfettario"));
                autobus.setCapienza(rs.getInt("Capienza"));
                autobusDisponibili.add(autobus);
            }
            return  autobusDisponibili;

        }
    }
}
