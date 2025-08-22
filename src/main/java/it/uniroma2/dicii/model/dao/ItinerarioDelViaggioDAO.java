package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Itinerario;
import it.uniroma2.dicii.model.domain.ItinerarioConTappe;
import it.uniroma2.dicii.model.domain.Tappa;

import java.sql.SQLException;

public class ItinerarioDelViaggioDAO implements GenericProcedureDAO <ItinerarioConTappe> {
    @Override
    public ItinerarioConTappe execute(Object... params) throws DAOException, SQLException {
        if (params.length < 1 || !(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("IDViaggio richiesto");
        }
        int idViaggio = (Integer) params[0];

        try (var conn = ConnectionFactory.getConnection();
             var cs = conn.prepareCall("{call visualizzaItinerarioConTappe(?)}")) {
            cs.setInt(1, idViaggio);

            try (var rs = cs.executeQuery()) {
                ItinerarioConTappe itinerario = null;
                while (rs.next()) {
                    if (itinerario == null) {
                        itinerario = new ItinerarioConTappe();
                        itinerario.setIdItinerario(rs.getInt("IDItinerario"));
                        itinerario.setDurata(rs.getInt("Durata"));
                        itinerario.setCosto(rs.getInt("Costo"));
                        itinerario.setNomeItinerario(rs.getString("NomeItinerario"));
                    }
                    Tappa tappa = new Tappa();
                    tappa.setNomeLocalita(rs.getString("NomeLocalita"));
                    tappa.setStato(rs.getString("Stato"));
                    tappa.setOrdine(rs.getInt("Ordine"));
                    tappa.setGiorni(rs.getInt("Giorni"));
                    itinerario.getTappe().add(tappa);
                }
                if (itinerario == null) {
                    throw new DAOException("Nessun itinerario trovato per il viaggio specificato");
                }
                return itinerario;
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
}
