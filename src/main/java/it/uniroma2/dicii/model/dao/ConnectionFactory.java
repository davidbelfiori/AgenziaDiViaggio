package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.model.domain.Role;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

//public class ConnectionFactory {
//    private static Connection connection;
//
//    private ConnectionFactory() {}
//
//    static {
//        // Does not work if generating a jar file
//        try (InputStream input = new FileInputStream("src/main/resources/db.properties")) {
//            Properties properties = new Properties();
//            properties.load(input);
//
//            String connection_url = properties.getProperty("CONNECTION_URL");
//            String user = properties.getProperty("LOGIN_USER");
//            String pass = properties.getProperty("LOGIN_PASS");
//
//            connection = DriverManager.getConnection(connection_url, user, pass);
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static Connection getConnection() throws SQLException {
//        return connection;
//    }
//
//    public static void changeRole(Role role) throws SQLException {
//        connection.close();
//
//        try (InputStream input = new FileInputStream("src/main/resources/db.properties")) {
//            Properties properties = new Properties();
//            properties.load(input);
//
//            String connection_url = properties.getProperty("CONNECTION_URL");
//            String user = properties.getProperty(role.name() + "_USER");
//            String pass = properties.getProperty(role.name() + "_PASS");
//
//            connection = DriverManager.getConnection(connection_url, user, pass);
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}


public class ConnectionFactory {
    private static final Properties props = new Properties();
    private static Role currentRole = Role.AGENTE; // Inizia sempre con il ruolo a permessi minimi

    static {

        try (InputStream input = new FileInputStream("src/main/resources/db.properties")) {

            if (input == null) {
                System.err.println("ERRORE: Impossibile trovare il file db.properties nel classpath.");
            } else {
                props.load(input);
            }

        } catch (IOException e) {
            System.err.println("Errore durante il caricamento del file db.properties");
            e.printStackTrace();
        }

    }

    public static void changeRole(Role role) {
        currentRole = role;
    }

    public static Connection getConnection() throws SQLException {
        String dbUrl = props.getProperty("CONNECTION_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {

            throw new SQLException("La URL del database non è stata trovata in db.properties.");
        }

        String user;
        String password;

        if (currentRole == Role.SEGRETERIA) {
            user = props.getProperty("db.user.segreteria");
            password = props.getProperty("db.password.segreteria");
        }else {
            user = props.getProperty("db.user.agente");
            password = props.getProperty("db.password.agente");
        }

        return DriverManager.getConnection(dbUrl, user, password);
    }
}
