package it.uniroma2.dicii.controller;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.dao.*;
import it.uniroma2.dicii.model.domain.*;
import it.uniroma2.dicii.view.SegreteriaView;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SegreteriaController implements Controller {
    Scanner input = new Scanner(System.in);

    @Override
    public void start() {
        // Cambia il ruolo della connessione a SEGRETERIA
        ConnectionFactory.changeRole(Role.SEGRETERIA);
        System.out.println("Welcome to the segreteria Controller!");

        while(true){
            int choice;
            // Mostra il menu principale
            choice = SegreteriaView.mostraMenuPrincipale();

            switch (choice) {
                case 1 -> creaViaggio();
                case 2 -> inserisciLocalita();
                case 3 -> inserisciAutobus();
                case 4 -> inserisciAlbergo();
                case 5 -> operazioniViaggio();
                case 6 -> creaItinerario();
                case 7-> visualizzaViaggiDisponibili();
                case 0 -> System.exit(0);
                default -> throw new RuntimeException("Invalid choice");
            }
        }
    }

    // Visualizza tutti i viaggi
    public void visualizzaViaggiDisponibili() {
        try {
            new AllViaggiDisponibiliDAO().execute();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    // Menu per operazioni su un viaggio specifico (selezionato tramite ID)
    private void operazioniViaggio() {

        System.out.println("inserisci l'id del viaggio su cui vuoi effettuare operazioni: ");
        int idViaggio = input.nextInt();
        //Controllo se il viaggio esiste
        Viaggio viaggio = null;
        try {
            viaggio = new VerificaEsistenzaViaggioDAO().execute(idViaggio);
        } catch (DAOException e) {
            System.out.println("Errore durante il recupero del viaggio: " + e.getMessage());
            return;
        }

        while (true) {
            int choice;
            choice = SegreteriaView.mostraSottoMenuViaggio();

            switch (choice) {
                case 1 -> generaReport(idViaggio);
                case 2 -> associaPernottamento(idViaggio);
                case 3 -> associaAutobus(idViaggio);
                case 0 -> {
                    return;
                }
                default -> throw new RuntimeException("Invalid choice");
            }
        }
    }

    // Associa un autobus a un viaggio
    private void associaAutobus(int idViaggio) {

        //Faccio visualizzare gli autobus disponibili
        System.out.println("Visualizzazione degli autobus disponibili:");

        try {
            // chiamo la classe DAO associata per ottenere gli autobus disponibili
            List<Autobus> autobusList = new AutobusDisponibiliDAO().execute(idViaggio);
            for (Autobus a : autobusList) {
                System.out.println(a.toString());
            }
        }catch (DAOException | SQLException e) {
            System.out.println("Errore durante la visualizzazione degli autobus: " + e.getMessage());
        }

        AutobusViaggio autobusViaggio = new AutobusViaggio();
        autobusViaggio.setCodiceViaggio(idViaggio);
        System.out.print("Inserisci la targa dell'autobus da associare al viaggio: ");
        autobusViaggio.setTarga(input.nextLine());

        try {
            AssociazioneAutobusViaggioResult autobusAssociato = new AssociaAutobusViaggioDAO().execute(autobusViaggio);
            System.out.println("Autobus associato al viaggio con successo: " + autobusAssociato.toString());
        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante l'associazione dell'autobus al viaggio: " + e.getMessage());
        }

    }

    private void associaPernottamento(int idViaggio) {

        //Mostro l'itinerario del viaggio
        try {
            ItinerarioConTappe itinerario = new ItinerarioDelViaggioDAO().execute(idViaggio);
            System.out.println("Itinerario del viaggio:");
            System.out.println("====== Itinerario ======");
            System.out.println("ID: " + itinerario.getIdItinerario());
            System.out.println("Nome: " + itinerario.getNomeItinerario());
            System.out.println("Durata: " + itinerario.getDurata() + " giorni");
            System.out.println("Costo: " + itinerario.getCosto() + " €");
            System.out.println("Tappe:");
            int i = 1;
            for (Tappa tappa : itinerario.getTappe()) {
                System.out.println("  Tappa " + i++ + ":");
                System.out.println("    Località: " + tappa.getNomeLocalita());
                System.out.println("    Stato: " + tappa.getStato());
                System.out.println("    Ordine: " + tappa.getOrdine());
                System.out.println("    Giorni: " + tappa.getGiorni());
            }
            System.out.println("==================");
            int j = 1;
            for (Tappa tappa : itinerario.getTappe()) {

                System.out.println("======"+"Tappa " + j++ + "======");
                System.out.println("    Località: " + tappa.getNomeLocalita());
                System.out.println("    Stato: " + tappa.getStato());
                System.out.println("    Ordine: " + tappa.getOrdine());
                System.out.println("    Giorni: " + tappa.getGiorni());
                System.out.println("==================");
                //Faccio visualizzare gli alberghi disponibili per la località della tappa
                System.out.println("======Visualizzazione degli alberghi disponibili per la località della tappa"+tappa.getNomeLocalita()+"======");
                try {

                    List<Albergo> albergoList = new AlberghiPerLocalitaDAO().execute(idViaggio,tappa.getNomeLocalita(), tappa.getStato());
                    if (albergoList.isEmpty()) {
                        System.out.println("Nessun albergo disponibile per la località " + tappa.getNomeLocalita() + ", " + tappa.getStato());
                        continue;
                    } else {
                        for (Albergo albergo : albergoList) {
                            System.out.println("=== Albergo ===");
                            System.out.println("ID: " + albergo.getCodiceAlbergo());
                            System.out.println("Nome: " + albergo.getNome());
                            System.out.println("Capienza: " + albergo.getCapienza());
                            System.out.println("Costo per notte: " + albergo.getCostoPN() + " €");
                            System.out.println("Referente: " + albergo.getReferente());
                            System.out.println("Via: " + albergo.getVia() + ", Numero: " + albergo.getNumero());
                            System.out.println("CAP: " + albergo.getCap());
                            System.out.println("Email: " + albergo.getEmail());
                            System.out.println("Fax: " + albergo.getFax());
                            System.out.println("Località: " + albergo.getNomeLocalita());
                            System.out.println("Stato: " + albergo.getStato());
                            System.out.println("================");
                        }
                        System.out.print("Inserisci il codice dell'albergo da associare alla tappa: ");
                        int codiceAlbergo = input.nextInt();
                        new AssociaPernottamentoDAO().execute(idViaggio, codiceAlbergo);
                    }
                }catch (DAOException e) {
                    System.out.println("Errore durante l'inserimento del pernottamento: " + e.getMessage());
                }


            }
        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante il recupero dell'itinerario del viaggio: " + e.getMessage());
            return;
        }


    }

    private void generaReport(int idViaggio) {
        try {
            new GeneraReportViaggioDAO().execute(idViaggio);
        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante la generazione del report: " + e.getMessage());
        }



    }

    private void creaItinerario() {
        Itinerario itinerario = new Itinerario();
        System.out.print("Inserisci il nome dell'itinerario: ");
        itinerario.setNomeItinerario(input.nextLine());
        System.out.print("Inserisci il costo per persona: ");
        itinerario.setCosto(Float.parseFloat(input.nextLine()));

        do {
            itinerario.setDurata(0);
            System.out.print("Inserisci il numero di giorni dell'itinerario: ");
            itinerario.setDurata(input.nextInt());
        }while (itinerario.getDurata() <=0 || itinerario.getDurata() > 7);//Assumiamo che un itinerario non possa durare più di 30 giorni

        try {
            Itinerario itinerarioInserito = new InserisciItinerarioDAO().execute(itinerario);
            System.out.println("Itinerario inserito con successo"+ itinerarioInserito.toString());
            while(true){
                int choice;
                choice = SegreteriaView.mostraSottoMenuItinerario();
                switch (choice) {
                    case 1 -> aggiungiTappaItinerario(itinerarioInserito.getIdItinerario());
                    case 0 -> {
                        return;
                    }
                    default -> throw new RuntimeException("Invalid choice");
                }
            }

        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante l'inserimento dell'itinerario: " + e.getMessage());
        }
    }

    private void aggiungiTappaItinerario(int idItnerario) {

        //Faccio visualizzare le località disponibili
        System.out.println("Visualizzazione delle località disponibili:");
        List<Localita> loocalita = new LocalitaDisponibiliDAO().execute();
        if (loocalita.isEmpty()) {
            System.out.println("Nessuna località disponibile. Inserisci prima una località.");
            return;
        }else{
            System.out.println("Località disponibili:");
            for (Localita l : loocalita) {
                System.out.println("--------------------");
                System.out.println("Località: " + l.getNome());
                System.out.println("Stato: " + l.getStato());
                System.out.println("Provincia: " + l.getProvincia());
                System.out.println("Regione: " + l.getRegione());
                System.out.println("--------------------");
            }
        }

        //Inserimento della tappa
        Tappa tappa = new Tappa();
        tappa.setIdItinerario(idItnerario);
        input.nextLine(); // Consuma il newline rimasto dopo l'input precedente
        System.out.print("Inserisci il nome della località della tappa: ");
        tappa.setNomeLocalita(input.nextLine());
        System.out.println("Inserisi lo stato della località della tappa: ");
        tappa.setStato(input.nextLine());
        System.out.print("Inserisci il numero di notti per la tappa: ");
        tappa.setGiorni(input.nextInt());
        System.out.println("Inserisci l'ordine della tappa (1 per la prima tappa, 2 per la seconda, etc.): ");
        tappa.setOrdine(input.nextInt());

        try {
            Tappa tappaInserita = new InserisciTappaItinerarioDAO().execute(tappa);
            System.out.println("Tappa inserita con successo: " + tappaInserita.toString());
        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante l'inserimento della tappa: " + e.getMessage());
        }


    }

    private void inserisciAlbergo() {
        Albergo albergo = new Albergo();
        System.out.print("Inserisci il nome dell'albergo: ");
        albergo.setNome(input.nextLine());
        System.out.print("Inserisci la capienza dell'albergo: ");
        try {
            albergo.setCapienza(Integer.parseInt(input.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Capienza non valida, deve essere un numero intero.");
            return;
        }
        System.out.print("Inserisci il costo per notte: ");
        albergo.setCostoPN( Float.parseFloat(input.nextLine()));
        System.out.print("Inserisci il referente dell'albergo: ");
        albergo.setReferente(input.nextLine());
        System.out.print("Inserisci la via dell'albergo (senza numero civico): ");
        albergo.setVia(input.nextLine());
        System.out.print("Inserisci il numero civico dell'albergo: ");
        albergo.setNumero(input.nextLine());
        System.out.print("Inserisci il CAP dell'albergo: ");
        albergo.setCap(input.nextLine());
        System.out.print("Inserisci l'email dell'albergo: ");
        albergo.setEmail(input.nextLine());
        System.out.print("Inserisci il fax dell'albergo: ");
        albergo.setFax(input.nextLine());
        System.out.print("Inserisci il nome della località dell'albergo: ");
        albergo.setNomeLocalita(input.nextLine());
        System.out.print("Inserisci lo stato dell'albergo: ");
        albergo.setStato(input.nextLine());

        try {
            Albergo albergoInseirto = new InserisciAlbergoDAO().execute(albergo);
            System.out.println("Albergo inserito con successo: " + albergoInseirto.toString());
        }catch (DAOException | SQLException e)
        {
            System.out.println("Errore durante l'inserimento dell'albergo: " + e.getMessage());
            return;
        }
    }

    private void inserisciAutobus() {

        Autobus autobus= new Autobus();
        System.out.print("Inserisci la targa dell'autobus: ");
        autobus.setTarga(input.nextLine());
        System.out.print("Inserisci la capienza dell'autobus: ");
        try {
            autobus.setCapienza(Integer.parseInt(input.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Capienza non valida, deve essere un numero intero.");
            return;
        }
        System.out.print("Inserisci il costo forfettario dell'autobus: ");
        try {
            autobus.setCostoForfettario(Float.parseFloat(input.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Costo non valido, deve essere un numero decimale.");
            return;
        }
        try {
            Autobus autobusInserito = new InserisciAutobusDAO().execute(autobus);
            System.out.println("Autobus inserito con successo: " + autobusInserito.toString());
        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante l'inserimento dell'autobus: " + e.getMessage());
        }


    }

    private void inserisciLocalita() {
        Localita localita = new Localita();
        System.out.print("Inserisci il nome della località: ");
        localita.setNome(input.nextLine());
        System.out.print("Inserisci il paese della località: ");
        localita.setStato(input.nextLine());
        System.out.print("Inserisci la provincia della località (può anche essere vuoto): ");
        localita.setProvincia(input.nextLine());
        System.out.print("Inserisci il Regione della località (può anche essere vuoto): ");
        localita.setRegione(input.nextLine());

        try{
            Localita newLocalita = new InserisciLocalitaDAO().execute(localita);
            System.out.println("Località inserita con successo: " + newLocalita.toString());
        }catch (DAOException | SQLException e) {
            System.out.println("Errore durante l'inserimento della località: " + e.getMessage());
        }


    }

    private void creaViaggio() {
        //Facciamo visualizzare gli itinerari disponibili e le loro tappe
        try {
            System.out.println("Visualizzazione degli itinerari disponibili:");
            new itinerariDisponibiliDAO().execute();
        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante la visualizzazione degli itinerari: " + e.getMessage());
            return;
        }
        Viaggio viaggio = new Viaggio();
        System.out.print("Inserisci l'id dell'itinerario da utilizzare: ");
        viaggio.setIdItinerario(input.nextInt());

        //inserimento della data di partenza
        java.sql.Date dataPartenza = null;
        while (dataPartenza == null) {
            System.out.print("Inserisci la data del viaggio (formato yyyy-MM-dd): ");
            String dataInput = input.nextLine();
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                java.util.Date parsed = sdf.parse(dataInput);
                dataPartenza = new java.sql.Date(parsed.getTime());
            } catch (java.text.ParseException e) {
                System.out.println("Data non valida, riprova.");
            }
        }
        viaggio.setDataPartenza(dataPartenza);

        try {
            Viaggio viaggioInserito = new InserisciViaggioDAO().execute(viaggio);
            System.out.println("Viaggio inserito con successo: " + viaggioInserito.toString());
        } catch (DAOException | SQLException e) {
            System.out.println("Errore durante l'inserimento del viaggio: " + e.getMessage());
        }
    }
}
