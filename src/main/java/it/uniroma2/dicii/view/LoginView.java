package it.uniroma2.dicii.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import it.uniroma2.dicii.model.domain.Credentials;

public class LoginView {
    public static Credentials authenticate() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("email: ");
        String email = reader.readLine();
        System.out.print("password: ");
        String password = reader.readLine();

        return new Credentials(email, password, null);
    }
}
