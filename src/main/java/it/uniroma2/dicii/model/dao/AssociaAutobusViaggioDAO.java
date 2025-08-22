package it.uniroma2.dicii.model.dao;

import it.uniroma2.dicii.exception.DAOException;
import it.uniroma2.dicii.model.domain.AssociazioneAutobusViaggioResult;
import it.uniroma2.dicii.model.domain.AutobusViaggio;

import java.sql.SQLException;
import java.sql.Types;

public class AssociaAutobusViaggioDAO implements GenericProcedureDAO<AssociazioneAutobusViaggioResult> {

    @Override
    public AssociazioneAutobusViaggioResult execute(Object... params) throws DAOException, SQLException {
        AutobusViaggio autobusViaggio = (AutobusViaggio) params[0];
        if (autobusViaggio == null) {
            throw new DAOException("AutobusViaggio non può essere null");
        } else {
            try (var conn = ConnectionFactory.getConnection();
                 var cs = conn.prepareCall("{call agenziadiviaggi.associaAutobusViaggio(?,?,?)}")) {
                cs.setInt(1, autobusViaggio.getCodiceViaggio());
                cs.setString(2, autobusViaggio.getTarga());
                cs.registerOutParameter(3, Types.VARCHAR);
                cs.executeQuery();
                String messaggio = cs.getString(3);
               return new AssociazioneAutobusViaggioResult(
                       autobusViaggio.getTarga(),
                       autobusViaggio.getCodiceViaggio(),
                       messaggio
               );
            } catch (SQLException e) {
                throw new DAOException(e.getMessage(), e);
            }
        }
    }
}
