package it.uniroma2.dicii.controller;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.dao.LoginProcedureDAO;
import it.uniroma2.dicii.model.domain.Credentials;
import it.uniroma2.dicii.view.LoginView;

import java.io.IOException;

public class LoginController implements Controller {

    Credentials cred = null;

    @Override
    public void start() {
      try {
          cred= LoginView.authenticate();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      try {
          cred = new LoginProcedureDAO().execute(cred.getEmail(), cred.getPassword());
      } catch (DAOException e) {
          throw new RuntimeException(e);
      }

    }
    public Credentials getCred() {
        return cred;
    }
}
