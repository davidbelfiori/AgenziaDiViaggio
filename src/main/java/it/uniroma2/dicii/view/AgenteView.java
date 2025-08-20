package it.uniroma2.dicii.view;

import java.io.IOException;
import java.util.Scanner;

public class AgenteView {



    public static int mostraMenu() throws IOException {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   Benvenuto nell'area Agente       ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Registra una prenotazione       ║");
        System.out.println("║ 2. Cancella una prenotazione       ║");
        System.out.println("║ 3. Visualizza viaggi disponibili   ║");
        System.out.println("║ 0. Esci                            ║");
        System.out.println("╚════════════════════════════════════╝");


        Scanner input = new Scanner(System.in);
        int choice = 0;
        while (true) {
            System.out.print("Seleziona un'opzione: ");
            choice = input.nextInt();
            if (choice >= 0 && choice <= 4) {
                break;
            }
            System.out.println("Invalid option");
        }

        return choice;
    }

    }

