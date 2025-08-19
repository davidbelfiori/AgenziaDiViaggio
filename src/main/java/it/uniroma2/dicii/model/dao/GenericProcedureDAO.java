package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;

import java.sql.SQLException;

public interface GenericProcedureDAO<P> {


    P execute(Object... params) throws DAOException, SQLException;
}
