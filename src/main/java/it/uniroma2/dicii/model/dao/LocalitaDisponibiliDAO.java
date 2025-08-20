package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.model.domain.Localita;

import java.util.ArrayList;
import java.util.List;

public class LocalitaDisponibiliDAO implements GenericProcedureDAO {

    @Override
    public List<Localita> execute(Object... params) {
       List<Localita> localita = new ArrayList<Localita>();

        try {
            var conn = ConnectionFactory.getConnection();
            var cs = conn.prepareCall("{call agenziadiviaggi.visualizzaLocalitaDisponibili()}");
            var rs = cs.executeQuery();

            while (rs.next()) {
                Localita l = new Localita(
                        rs.getString("Nome"),
                        rs.getString("Stato"),
                        rs.getString("Regione"),
                        rs.getString("Provincia")
                );
                localita.add(l);
            }
            return localita;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
