package it.uniroma2.dicii.view;

import java.util.Scanner;

public class SegreteriaView {

    public static int mostraMenuPrincipale() {
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

        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Seleziona un'opzione: ");
            choice = input.nextInt();
            if (choice >= 0 && choice <= 6) {
                break;
            }
            System.out.println("Invalid option");
        }

        return choice;
    }

    public static int mostraSottoMenuViaggio() {
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

        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Seleziona un'opzione: ");
            choice = input.nextInt();
            if (choice >= 0 && choice <= 3) {
                break;
            }
            System.out.println("Invalid option");
        }

        return choice;
    }

    public static int mostraSottoMenuItinerario() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   Gestione Itinerario Creato       ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Aggiungi tappa (località)       ║");
        System.out.println("║ 0. Torna al menu principale        ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Seleziona un'opzione: ");

        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Seleziona un'opzione: ");
            choice = input.nextInt();
            if (choice >= 0 && choice <= 1) {
                break;
            }
            System.out.println("Invalid option");
        }

        return choice;
    }


}