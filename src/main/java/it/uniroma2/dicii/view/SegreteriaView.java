package it.uniroma2.dicii.view;

import java.util.Scanner;

public class SegreteriaView {
    private final Scanner scanner = new Scanner(System.in);

    public void mostraMenuPrincipale() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║         Area Segreteria - Menu             ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.println("║ 1. Crea un nuovo viaggio                   ║");
        System.out.println("║ 2. Inserisci località                      ║");
        System.out.println("║ 3. Inserisci autobus                       ║");
        System.out.println("║ 4. Inserisci albergo                       ║");
        System.out.println("║ 5. Operazioni su un viaggio specifico      ║");
        System.out.println("║ 6. Crea itinerario                         ║");
        System.out.println("║ 0. Esci                                    ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.print("Seleziona un'opzione: ");
    }

    public void mostraSottoMenuViaggio() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   Operazioni su Viaggio            ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Genera report viaggio           ║");
        System.out.println("║ 2. Associa pernottamento           ║");
        System.out.println("║ 3. Associa autobus al viaggio      ║");
        System.out.println("║ 0. Torna al menu principale        ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Seleziona un'opzione: ");
    }

    public void mostraSottoMenuItinerario() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   Gestione Itinerario Creato       ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Aggiungi tappa (località)       ║");
        System.out.println("║ 0. Torna al menu principale        ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Seleziona un'opzione: ");
    }

    public int leggiScelta() {
        int scelta = -1;
        try {
            scelta = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero.");
        }
        return scelta;
    }

    public String chiediInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void mostraMessaggio(String messaggio) {
        System.out.println(messaggio);
    }
}