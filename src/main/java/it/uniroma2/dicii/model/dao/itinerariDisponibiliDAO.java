package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class itinerariDisponibiliDAO implements  GenericProcedureDAO{

    @Override
    public Object execute(Object... params) throws DAOException, SQLException {
        try(Connection conn = ConnectionFactory.getConnection();
            var cs = conn.prepareCall("{call visualizzaItinerariDisponibili()}")) {
           ResultSet rs= cs.executeQuery();

            int lastId = -1;
            String nomeItinerario = null;
            double costo = 0;
            StringBuilder tappe = new StringBuilder();

            while (rs.next()) {
                int id = rs.getInt("IDItinerario");
                if (id != lastId && lastId != -1) {
                    // Stampa l'itinerario precedente
                    System.out.println("Itinerario: " + nomeItinerario + " (ID: " + lastId + ", Costo: " + costo + ")");
                    System.out.println("Tappe:");
                    System.out.print(tappe);
                    System.out.println("-----------------------------");
                    tappe.setLength(0); // reset tappe
                }
                lastId = id;
                nomeItinerario = rs.getString("NomeItinerario");
                costo = rs.getDouble("Costo");
                String localita = rs.getString("NomeLocalita");
                String stato = rs.getString("Stato");
                int ordine = rs.getInt("Ordine");
                tappe.append("  ").append(ordine).append(". ").append(localita).append(" (").append(stato).append(")\n");
            }
            // Stampa l'ultimo itinerario
            if (lastId != -1) {
                System.out.println("Itinerario: " + nomeItinerario + " (ID: " + lastId + ", Costo: " + costo + ")");
                System.out.println("Tappe:");
                System.out.print(tappe);
                System.out.println("-----------------------------");
            }
            return null;

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
}
