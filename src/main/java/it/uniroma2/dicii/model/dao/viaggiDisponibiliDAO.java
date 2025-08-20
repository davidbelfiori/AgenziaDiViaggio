package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class viaggiDisponibiliDAO implements GenericProcedureDAO{

    @Override
    public Object execute(Object... params) throws DAOException, SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call agenziadiviaggi.visualizzaViaggiDisponibili()}")) {
            ResultSet rs = cs.executeQuery();
            int lastIdViaggio = -1;
            while (rs.next()) {
                int idViaggio = rs.getInt("IDViaggio");
                if (idViaggio != lastIdViaggio) {
                    // Stampa intestazione viaggio
                    System.out.println("\n-----------------------------");
                    System.out.println("ID Viaggio: " + idViaggio);
                    System.out.println("Itinerario: " + rs.getString("NomeItinerario"));
                    System.out.println("Data partenza: " + rs.getDate("DataPartenza"));
                    System.out.println("Stato: " + rs.getString("Stato"));
                    System.out.println("Tappe:");
                    lastIdViaggio = idViaggio;
                }
                // Stampa tappa
                System.out.println("  - Ordine: " + rs.getInt("Ordine") +
                        ", Località: " + rs.getString("NomeLocalita") +
                        ", Stato: " + rs.getString("StatoLocalita") +
                        ", Giorni: " + rs.getInt("Giorni"));
            }
            cs.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Errore SQL: " + e.getMessage());
            throw new DAOException("Errore durante la visualizzazione dei viaggi disponibili", e);
        }

        return null;
    }
}
