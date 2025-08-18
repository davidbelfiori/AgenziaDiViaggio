package it.uniroma2.dicii.controller;

import it.uniroma2.dicii.model.dao.ConnectionFactory;
import it.uniroma2.dicii.model.domain.Role;

import java.sql.SQLException;

public class AgenteController implements Controller{

    @Override
    public void start() {

        try {
            ConnectionFactory.changeRole(Role.AGENTE);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Welcome to the Agente Controller!");
    }
}
