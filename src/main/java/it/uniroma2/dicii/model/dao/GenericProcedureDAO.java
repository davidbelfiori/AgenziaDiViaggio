package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;

import java.sql.SQLException;

public interface GenericProcedureDAO<P> {

    /**
     * Executes a stored procedure with the given parameters.
     *
     * @param params the parameters to pass to the stored procedure
     * @return the result of the stored procedure execution
     * @throws DAOException if there is an error executing the procedure
     */
    P execute(Object... params) throws DAOException, SQLException;
}
