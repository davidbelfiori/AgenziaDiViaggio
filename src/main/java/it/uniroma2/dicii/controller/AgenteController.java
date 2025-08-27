package it.uniroma2.dicii.controller;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.dao.ConnectionFactory;
import it.uniroma2.dicii.model.dao.EliminaPrenotazioneDAO;
import it.uniroma2.dicii.model.dao.InserisciPrenotazioneDAO;
import it.uniroma2.dicii.model.domain.Role;
import  it.uniroma2.dicii.model.dao.viaggiDisponibiliDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import it.uniroma2.dicii.view.AgenteView;
public class AgenteController implements Controller {
    Scanner input = new Scanner(System.in);

    @Override
    public void start() {

            ConnectionFactory.changeRole(Role.AGENTE);

        System.out.println("Welcome to the Agente Controller!");


        while(true){
            int choice;
            try {
                choice = AgenteView.mostraMenu();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            switch (choice) {
                case 1 -> registraPrenotazione();
                case 2 -> cancellaPrenotazione();
                case 3 -> visualizzaViaggiDisponibili();
                case 0 -> System.exit(0);
                default -> throw new RuntimeException("Invalid choice");
            }
        }

    }

    public void visualizzaViaggiDisponibili() {
        try {
            new viaggiDisponibiliDAO().execute();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            return;
        }
        System.out.println("Viaggi disponibili visualizzati con successo.");

    }

    public void cancellaPrenotazione() {

            System.out.print("Inserisci il codice di disdetta della prenotazione da cancellare: ");
            String codiceDisdetta = input.nextLine();
            try {
                new EliminaPrenotazioneDAO().execute(codiceDisdetta);
                System.out.println("Prenotazione eliminata con successo.");
            } catch (DAOException e) {
                System.err.println("Errore: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Errore SQL: " + e.getMessage());
            }
        }


    public void registraPrenotazione() {

      try {
          new viaggiDisponibiliDAO().execute();
      } catch (Exception e) {
           System.err.println(e.getMessage());
           return;
      }
        int idViaggio;
        int numeroPasseggeri;
        String CodiceDisdetta;
        System.out.println("Inserisci l'ID del viaggio: ");
        idViaggio = input.nextInt();
        System.out.println("Inserisci il numero di passeggeri: ");
        numeroPasseggeri = input.nextInt();
        try {
            CodiceDisdetta = (String) new  InserisciPrenotazioneDAO().execute(numeroPasseggeri, idViaggio);
            System.out.println("Prenotazione inserita con successo! Codice disdetta: " + CodiceDisdetta);
        } catch (DAOException e) {
            System.err.println("Errore: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
        }
    }
}


