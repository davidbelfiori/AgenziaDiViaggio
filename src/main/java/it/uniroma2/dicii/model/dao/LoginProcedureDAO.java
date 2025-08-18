package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.Credentials;
import it.uniroma2.dicii.model.domain.Role;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginProcedureDAO implements GenericProcedureDAO<Credentials> {

    @Override
    public Credentials execute(Object... params) throws DAOException {
        String email = (String) params[0];
        String password = (String) params[1];
        int role;

        try {
            Connection conn = ConnectionFactory.getConnection();
            CallableStatement cs = conn.prepareCall("{call login(?,?,?)}");
            cs.setString(1, email);
            cs.setString(2, password);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.executeQuery();

            //In base se il risutato sia AV o AM passo il ruolo corretto
            String roleString = cs.getString(3);
            if (roleString.equals("AV")) {
                role = Role.AGENTE.getId();
            } else if (roleString.equals("AM")) {
                role = Role.SEGRETERIA.getId();
            } else {
                throw new DAOException("Invalid role returned from login procedure: " + roleString);
            }
        } catch(SQLException e) {
            throw new DAOException("Login error: " + e.getMessage());
        }


        return new Credentials(email, password, Role.fromInt(role));
    }
}
