-- Tabelle e trigger per la gestione del database di un'agenzia di viaggi

create table autobus
(
    Targa            varchar(10) not null
        primary key,
    Capienza         int         not null,
    CostoForfettario float       not null
);

create table itinerario
(
    IDItinerario   int auto_increment
        primary key,
    Durata         int          not null,
    Costo          float        not null,
    NomeItinerario varchar(255) not null,
    constraint table_name_pk_2
        unique (NomeItinerario)
);

create table localita
(
    Nome      varchar(50) not null,
    Stato     varchar(60) not null,
    Provincia varchar(2)  null,
    Regione   varchar(25) null,
    primary key (Nome, Stato)
);

create table albergo
(
    CodiceAlbergo int auto_increment
        primary key,
    Nome          varchar(200) not null,
    Capienza      int          not null,
    CostoPN       float        not null,
    Referente     varchar(100) not null,
    Via           varchar(60)  not null,
    Numero        varchar(10)  not null,
    Cap           varchar(20)  null,
    Email         varchar(100) not null,
    Fax           varchar(20)  not null,
    NomeLocalita  varchar(50)  not null,
    Stato         varchar(60)  not null,
    constraint albergo_ibfk_1
        foreign key (NomeLocalita, Stato) references localita (Nome, Stato)
);

create index NomeLocalita
    on albergo (NomeLocalita, Stato);

create table tappa
(
    IDItinerario int         not null,
    NomeLocalita varchar(50) not null,
    Stato        varchar(60) not null,
    Ordine       int         not null,
    Giorni       int         not null,
    primary key (Stato, IDItinerario, NomeLocalita),
    constraint Tappa_Tappa_NomeLocalità_Stato_fk
        foreign key (NomeLocalita, Stato) references localita (Nome, Stato),
    constraint Tappa_itinerario_IDItinerario_fk
        foreign key (IDItinerario) references itinerario (IDItinerario)
            on delete cascade
);

create table user
(
    Email    varchar(100)      not null
        primary key,
    Password varchar(50)       not null,
    Ruolo    enum ('AM', 'AV') not null
);

create index user_Email_Password_index
    on user (Email, Password);

create table viaggio
(
    IDViaggio    int auto_increment
        primary key,
    DataPartenza date                                         not null,
    DataRientro  date                                         not null,
    Stato        enum ('PROGRAMMATO', 'INCORSO', 'TERMINATO') not null,
    IDItinerario int                                          not null,
    constraint Viaggio_itinerario_IDItinerario_fk
        foreign key (IDItinerario) references itinerario (IDItinerario)
);

create table autobusviaggio
(
    CodiceViaggio int         not null,
    Targa         varchar(10) not null,
    primary key (CodiceViaggio, Targa),
    constraint AutobusViaggio_autobus_Targa_fk
        foreign key (Targa) references autobus (Targa),
    constraint AutobusViaggio_viaggio_IDViaggio_fk
        foreign key (CodiceViaggio) references viaggio (IDViaggio)
            on delete cascade
);

create trigger check_autobus_disponibile
    before insert
    on autobusviaggio
    for each row
BEGIN
    DECLARE viaggio_start DATE;
    DECLARE viaggio_end DATE;

    -- Ottieni le date del viaggio che si vuole assegnare all'autobus
    SELECT DataPartenza, DataRientro
    INTO viaggio_start, viaggio_end
    FROM viaggio
    WHERE IDViaggio = NEW.CodiceViaggio;

    -- Verifica se l'autobus è già assegnato ad altri viaggi con date sovrapposte
    IF EXISTS (
        SELECT 1
        FROM autobusviaggio av
                 JOIN viaggio v ON av.CodiceViaggio = v.IDViaggio
        WHERE av.Targa = NEW.Targa
          AND (
            (v.DataPartenza <= viaggio_end AND v.DataRientro >= viaggio_start)
            )
    )
    THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'Autobus non disponibile per le date del viaggio selezionato!';
    END IF;
END;

create table pernottamento
(
    IDViaggio     int         not null,
    CodiceAlbergo int         not null,
    NomeLocalita  varchar(50) not null,
    Stato         varchar(60) not null,
    DataCheckIn   date        not null,
    DataCheckOut  date        not null,
    primary key (IDViaggio, CodiceAlbergo, NomeLocalita, Stato),
    constraint pernottamento_ibfk_1
        foreign key (IDViaggio) references viaggio (IDViaggio)
            on delete cascade,
    constraint pernottamento_ibfk_2
        foreign key (CodiceAlbergo) references albergo (CodiceAlbergo),
    constraint pernottamento_ibfk_3
        foreign key (NomeLocalita, Stato) references localita (Nome, Stato)
);

create index CodiceAlbergo
    on pernottamento (CodiceAlbergo);

create index NomeLocalita
    on pernottamento (NomeLocalita, Stato);

create index pernottamento_IDViaggio_index
    on pernottamento (IDViaggio);

create trigger check_albergo_capienza
    before insert
    on pernottamento
    for each row
BEGIN
    DECLARE tot_partecipanti INT;
    DECLARE albergo_capienza INT;

    -- Calcola il numero totale di partecipanti al viaggio
    SELECT IFNULL(SUM(NumeroPartecipanti),0)
    INTO tot_partecipanti
    FROM prenotazioni
    WHERE CodiceViaggio = NEW.IDViaggio;

    -- Prendi la capienza dell'albergo selezionato
    SELECT Capienza
    INTO albergo_capienza
    FROM albergo
    WHERE CodiceAlbergo = NEW.CodiceAlbergo;

    -- Se l'albergo non ha abbastanza posti, blocca l'inserimento
    IF albergo_capienza < tot_partecipanti THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'La capienza dell\'albergo selezionato non è sufficiente per i partecipanti al viaggio!';
    END IF;
END;

create table prenotazioni
(
    CodicePrenotazione int auto_increment
        primary key,
    NumeroPartecipanti int         not null,
    CodiceDisdetta     varchar(20) not null,
    CodiceViaggio      int         not null,
    constraint prenotazioni_pk
        unique (CodiceDisdetta),
    constraint prenotazioni_viaggio_IDViaggio_fk
        foreign key (CodiceViaggio) references viaggio (IDViaggio)
            on delete cascade
);

create trigger check_cancellation
    before delete
    on prenotazioni
    for each row
begin
    declare var_StartDate date;
    declare var_status  varchar(20);
    -- Recupero la data e lo stato del viaggio legato alla prenotazione che sto cancellando
    SELECT v.DataPartenza, v.Stato
    INTO var_StartDate, var_status
    FROM viaggio v
    WHERE v.IDViaggio = OLD.CodiceViaggio
    LIMIT 1;

    IF var_status = 'INCORSO' AND DATEDIFF(var_StartDate, NOW()) <= 20 THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'Impossibile cancellare una prenotazione nei venti giorni prima della partenza';
    ELSEIF var_status = 'TERMINATO' THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'Impossibile cancellare una prenotazione di un viaggio terminato';
    END IF;
end;

create trigger check_prenotazione
    before insert
    on prenotazioni
    for each row
BEGIN
    DECLARE var_StartDate DATE;

    -- Recupera la data di partenza del viaggio associato
    SELECT DataPartenza
    INTO var_StartDate
    FROM viaggio
    WHERE IDViaggio = NEW.CodiceViaggio
    LIMIT 1;

    -- Se la partenza è nei prossimi 20 giorni, blocca l'inserimento
    IF DATEDIFF(var_StartDate, CURDATE()) <= 20 THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'Impossibile effettuare una prenotazione nei venti giorni prima della partenza';
    END IF;
END;

create index viaggio_Stato_index
    on viaggio (Stato);

create trigger check_date_viaggio
    before insert
    on viaggio
    for each row
begin
    if NEW.DataRientro<NEW.DataPartenza then signal sqlstate '45001' set message_text = 'La data del rientro non può
precedere la data di partenza';
    end if;
end;

-- Stored Procedures --

create procedure aggiungiTappaItinerario(IN p_IDItinerario int, IN p_NomeLocalita varchar(50), IN p_Stato varchar(60),
                                         IN p_Ordine int, IN p_Giorni int)
BEGIN
    DECLARE v_DurataItinerario INT;
    DECLARE v_SommaGiorni INT;
    DECLARE v_MaxOrdine INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE ;
    START TRANSACTION;

    -- Controllo esistenza itinerario
    IF NOT EXISTS (SELECT 1 FROM itinerario WHERE IDItinerario = p_IDItinerario) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Itinerario non trovato!';
        -- Controllo esistenza località
    ELSEIF NOT EXISTS (SELECT 1 FROM localita WHERE Nome= p_NomeLocalita AND Stato = p_Stato) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Località non trovata!';
        -- Controllo ordine consecutivo
    ELSE
        SELECT IFNULL(MAX(Ordine), 0) INTO v_MaxOrdine
        FROM tappa
        WHERE IDItinerario = p_IDItinerario;

        -- Se l'ordine non è consecutivo rispetto all'ultimo inserito
        IF p_Ordine <> v_MaxOrdine + 1 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ordine non consecutivo!';
        ELSE
            -- Controllo durata itinerario
            SELECT Durata INTO v_DurataItinerario
            FROM itinerario
            WHERE IDItinerario = p_IDItinerario;

            IF p_Giorni > v_DurataItinerario THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Durata tappa superiore alla durata itinerario!';
            ELSE
                -- Controllo somma giorni tappe
                SELECT IFNULL(SUM(Giorni), 0) INTO v_SommaGiorni
                FROM tappa
                WHERE IDItinerario = p_IDItinerario;

                -- Se la somma dei giorni delle tappe supera la durata dell'itinerario
                -- o se la somma dei giorni delle tappe più i giorni della nuova tappa
                -- supera 7 giorni, l'inserimento non è consentito
                IF p_Giorni=0 THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'I giorni della tappa non possono essere zero!';
                ELSEIF v_SommaGiorni + p_Giorni > 7 OR v_SommaGiorni + p_Giorni > v_DurataItinerario THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Somma giorni tappe superiore alla durata itinerario!';
                ELSE
                    INSERT INTO tappa (IDItinerario, NomeLocalita, Stato, Ordine, Giorni)
                    VALUES (p_IDItinerario, p_NomeLocalita, p_Stato, p_Ordine, p_Giorni);
                    COMMIT;
                END IF;
            END IF;
        END IF;
    END IF;
END;

create procedure associaAutobusViaggio(IN p_CodiceViaggio int, IN p_Targa varchar(10), OUT p_Messaggio varchar(255))
BEGIN


    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
    START TRANSACTION;

    -- Controllo se il viaggio esiste
    IF NOT EXISTS (SELECT 1 FROM viaggio WHERE IDViaggio = p_CodiceViaggio) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viaggio non trovato!';
    ELSEIF (SELECT viaggio.Stato from viaggio WHERE viaggio.IDViaggio = p_CodiceViaggio) <> 'INCORSO' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Impossibile associare un autobus ad un viaggio se non mancano meno di 20 giorni alla data di partenza!';
        -- Controllo se l'autobus esiste
    ELSEIF NOT EXISTS (SELECT 1 FROM autobus WHERE Targa = p_Targa) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Autobus non trovato con questa targa!';
        -- Controllo se l'autobus è già associato al viaggio
    ELSEIF EXISTS (SELECT 1 FROM autobusviaggio WHERE CodiceViaggio = p_CodiceViaggio AND Targa = p_Targa) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Autobus già associato a questo viaggio!';
        -- Controllo se la somma dei posti degli autobus assocciati è almeno uguale  al numero di partecipanti
    ELSEIF  (SELECT sum(NumeroPartecipanti) FROM prenotazioni WHERE CodiceViaggio = p_CodiceViaggio) > (SELECT COALESCE(SUM(Capienza),0) +
                                                                                                               (SELECT Capienza FROM autobus WHERE Targa = p_Targa)
                                                                                                        FROM autobus join agenziadiviaggi.autobusviaggio a on autobus.Targa = a.Targa join agenziadiviaggi.viaggio v on v.IDViaggio = a.CodiceViaggio
                                                                                                        WHERE a.CodiceViaggio = p_CodiceViaggio)
    THEN
        -- Associo l'autobus al viaggio
        INSERT INTO autobusviaggio (CodiceViaggio, Targa)
        VALUES (p_CodiceViaggio, p_Targa);
        SET p_Messaggio = 'La somma dei posti degli autobus associati è inferiore al numero di partecipanti, aggiungi un altro autobus!';
        COMMIT;

    ELSE
        -- Associo l'autobus al viaggio
        INSERT INTO autobusviaggio (CodiceViaggio, Targa)
        VALUES (p_CodiceViaggio, p_Targa);
        SET p_Messaggio = 'Autobus associato al viaggio con successo!';
        COMMIT;
    END IF;
END;

create procedure associaPernottamento(IN p_IDViaggio int, IN p_CodiceAlbergo int)
BEGIN

    DECLARE v_NomeLocAlbergo varchar(50);
    DECLARE v_StatoAlbergo varchar(60);
    DECLARE v_DataCheckIn DATE;
    DECLARE v_DataCheckOut DATE;
    DECLARE v_Durata INT;
    DECLARE v_GiorniPrec INT;
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE ;
    START TRANSACTION;

    -- Recupero il nome località dell'albergo
    SELECT albergo.NomeLocalita, albergo.Stato INTO v_NomeLocAlbergo, v_StatoAlbergo
    FROM albergo
    WHERE albergo.CodiceAlbergo = p_CodiceAlbergo;

    -- Controllo se il viaggio esiste
    IF NOT EXISTS (SELECT 1 FROM viaggio WHERE IDViaggio = p_IDViaggio) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viaggio non trovato!';
    ELSEIF (SELECT viaggio.Stato from viaggio WHERE viaggio.IDViaggio = p_IDViaggio) <> 'INCORSO' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Impossibile associare un Pernottamento ad un viaggio se non mancano meno di 20 giorni alla data di partenza!';

        -- Controllo se l'albergo esiste
    ELSEIF NOT EXISTS (SELECT 1 FROM albergo WHERE CodiceAlbergo = p_CodiceAlbergo) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Albergo non trovato con questo codice!';
        -- Controllo se la località esiste
    ELSEIF NOT EXISTS (SELECT 1 FROM localita WHERE Nome = v_NomeLocAlbergo AND Stato = v_StatoAlbergo) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Località non trovata!';
        -- Controllo se il pernottamento esiste già
    ELSEIF EXISTS (SELECT 1 FROM pernottamento WHERE IDViaggio = p_IDViaggio  AND NomeLocalita = v_NomeLocAlbergo AND Stato = v_StatoAlbergo) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pernottamento già associato a questo viaggio!';
    ELSE


        -- Calcolo la somma dei giorni delle tappe precedenti
        SELECT IFNULL(SUM(Giorni), 0) INTO v_GiorniPrec
        FROM tappa
        WHERE IDItinerario = (
            SELECT IDItinerario FROM viaggio WHERE IDViaggio = p_IDViaggio
        )
          AND Ordine < (
            SELECT Ordine FROM tappa
            WHERE IDItinerario = (
                SELECT IDItinerario FROM viaggio WHERE IDViaggio = p_IDViaggio
            )
              AND NomeLocalita = v_NomeLocAlbergo AND Stato = v_StatoAlbergo
        );

        -- Calcolo la durata della tappa corrente
        SELECT Giorni INTO v_Durata
        FROM tappa
        WHERE IDItinerario = (
            SELECT IDItinerario FROM viaggio WHERE IDViaggio = p_IDViaggio
        )
          AND NomeLocalita = v_NomeLocAlbergo AND Stato = v_StatoAlbergo;

        -- Calcolo le date di check-in e check-out
        SELECT DataPartenza + INTERVAL v_GiorniPrec DAY INTO v_DataCheckIn
        FROM viaggio WHERE IDViaggio = p_IDViaggio;

        SET v_DataCheckOut = v_DataCheckIn + INTERVAL v_Durata DAY;

        -- Inserisco il pernottamento
        INSERT INTO pernottamento (IDViaggio, CodiceAlbergo, NomeLocalita, Stato, DataCheckIn, DataCheckOut)
        VALUES (p_IDViaggio, p_CodiceAlbergo, v_NomeLocAlbergo, v_StatoAlbergo, v_DataCheckIn, v_DataCheckOut);
        COMMIT;
    END IF;

end;

create procedure cancellazionePrenotazione(IN p_codiceDisdetta varchar(20))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
    START TRANSACTION;


    IF NOT EXISTS (
        SELECT 1 FROM prenotazioni WHERE CodiceDisdetta = p_codiceDisdetta
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Prenotazione non trovata con questo codice di disdetta!';
    ELSE
        DELETE FROM prenotazioni WHERE CodiceDisdetta = p_codiceDisdetta;
        COMMIT;
    END IF;
END;

create procedure creaItinerario(IN p_Durata int, IN p_Costo float, IN p_NomeItinerario varchar(255),
                                OUT itinerarioID int)
BEGIN

    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
    START TRANSACTION;

    -- Controllo se l'itinerario esiste già
    IF EXISTS (SELECT 1 FROM itinerario WHERE NomeItinerario = p_NomeItinerario) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Itinerario già presente con questo nome!';
    ELSEIF p_Durata>7 OR p_Durata<1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Durata non valida! Deve essere compresa tra 1 e 7 giorni.';
    ELSEIF p_Costo<0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Costo non valido! Deve essere un valore positivo.';
    ELSE
        -- Inserisco l'itinerario nella tabella
        INSERT INTO itinerario (Durata, Costo, NomeItinerario)
        VALUES (p_Durata, p_Costo, p_NomeItinerario);
        -- Recupero l'ID dell'itinerario appena inserito
        SET itinerarioID = LAST_INSERT_ID();
    END IF;
    -- Commit della transazione
    COMMIT;
end;

create procedure creaViaggio(IN p_DataPartenza date, IN p_IDItinerario int, OUT p_IDViaggio int, OUT p_DataRientro date)
BEGIN
    DECLARE v_Durata INT;
    DECLARE v_DataRientro DATE;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
    START TRANSACTION;

    -- Controllo se l'itinerario esiste
    IF NOT EXISTS (SELECT 1 FROM itinerario WHERE IDItinerario = p_IDItinerario) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Itinerario non trovato!';
    ELSE
        -- Controllo se esiste già un viaggio programmato per l'itinerario
        IF EXISTS (SELECT 1 FROM viaggio WHERE IDItinerario = p_IDItinerario AND DataPartenza = p_DataPartenza) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Esiste già un viaggio programmato per questo itinerario con questa data di partenza!';
        ELSEIF
            -- Controllo se la data di partenza è nel passato
            DATEDIFF(p_DataPartenza,CURDATE()) <= 20 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'La data di partenza deve essere almeno 20 giorni nel futuro!';
        ELSE
            -- Calcolo la data di rientro aggiungendo la durata dell'itinerario
            SELECT Durata INTO v_Durata FROM itinerario WHERE IDItinerario = p_IDItinerario;
            SET v_DataRientro = DATE_ADD(p_DataPartenza, INTERVAL v_Durata DAY);
            -- Inserisco il nuovo viaggio
            INSERT INTO viaggio (DataPartenza, DataRientro, Stato, IDItinerario)
            VALUES (p_DataPartenza, v_DataRientro, 'PROGRAMMATO', p_IDItinerario);

            -- Recupero l'ID del nuovo viaggio
            SET p_IDViaggio = LAST_INSERT_ID();
            SET p_DataRientro = v_DataRientro;
            COMMIT;
        END IF;
    END IF;
end;

create procedure generaReportViaggio(IN p_IDViaggio int, OUT p_Report text)
BEGIN
    DECLARE v_NomeItinerario VARCHAR(255);
    DECLARE v_DataPartenza DATE;
    DECLARE v_DataRientro DATE;
    DECLARE v_CostoAlberghi FLOAT;
    DECLARE v_CostoAutobus FLOAT;
    DECLARE v_CostoTotale FLOAT;
    DECLARE v_CostoPerPartecipante FLOAT;
    DECLARE v_NumeroPartecipanti INT;
    DECLARE v_EntratePartecipanti FLOAT;
    DECLARE v_Esito ENUM('Profitto','Perdita');
    DECLARE v_Bilancio FLOAT;

    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
    START TRANSACTION;

    -- Controllo se il viaggio esiste
    IF NOT EXISTS (SELECT 1 FROM viaggio WHERE IDViaggio = p_IDViaggio) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viaggio non trovato!';
    ELSEIF
        -- Controllo se il viaggio è già concluso
        (SELECT Stato FROM viaggio WHERE IDViaggio = p_IDViaggio) <> 'TERMINATO' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Il viaggio non è ancora concluso!';
    ELSE
        -- Mi ricavo il numero di partecipanti
        SELECT sum(NumeroPartecipanti) INTO v_NumeroPartecipanti
        FROM prenotazioni
        WHERE CodiceViaggio = p_IDViaggio;

        -- Mi ricavo il nome dell'itinerario e il costo totale
        SELECT i.NomeItinerario, i.Costo INTO v_NomeItinerario, v_CostoPerPartecipante
        FROM itinerario i
                 JOIN viaggio v ON i.IDItinerario = v.IDItinerario
        WHERE v.IDViaggio = p_IDViaggio;


        -- Mi ricavo le date di partenza e rientro
        SELECT DataPartenza, DataRientro INTO v_DataPartenza, v_DataRientro
        FROM viaggio
        WHERE IDViaggio = p_IDViaggio;

        -- Calcolo il numero di notti
        SET @numNotti = DATEDIFF(v_DataRientro, v_DataPartenza) + 1;

        -- Calcolo il costo totale degli alberghi (tutte le strutture, tutte le notti, tutti i partecipanti)
        SELECT COALESCE(SUM(a.CostoPN),0) * @numNotti * COALESCE(v_NumeroPartecipanti,0) INTO v_CostoAlberghi
        FROM pernottamento p
                 JOIN albergo a ON p.CodiceAlbergo = a.CodiceAlbergo
        WHERE p.IDViaggio = p_IDViaggio;

        -- Calcolo il costo totale degli autobus
        SELECT SUM(a.CostoForfettario)*(DATEDIFF(v_DataRientro, v_DataPartenza)) INTO v_CostoAutobus
        FROM autobus a
                 JOIN autobusviaggio av ON a.Targa = av.Targa
        WHERE av.CodiceViaggio = p_IDViaggio;

        -- Calcolo il costo totale del viaggio
        SET v_CostoTotale = v_CostoAlberghi + v_CostoAutobus;

        -- Calcolo le entrate totali dai partecipanti
        SET v_EntratePartecipanti = v_CostoPerPartecipante * v_NumeroPartecipanti;

        -- Calcolo il bilancio
        SET v_Bilancio = v_EntratePartecipanti - v_CostoTotale;
        -- Determino l'esito del viaggio
        IF v_Bilancio > 0 THEN
            SET v_Esito = 'Profitto';
        ELSEIF v_Bilancio < 0 THEN
            SET v_Esito = 'Perdita';
        ELSE
            SET v_Esito = 'Pareggio';
        END IF;
        -- Creo il report
        SET p_Report = CONCAT(
                'Report Viaggio ID: ', p_IDViaggio, '\n',
                'Nome Itinerario: ', v_NomeItinerario, '\n',
                'Data Partenza: ', v_DataPartenza, '\n',
                'Data Rientro: ', v_DataRientro, '\n',
                'Numero Partecipanti: ', v_NumeroPartecipanti, '\n',
                'Costo per Partecipante: ', v_CostoPerPartecipante, '\n',
                'Costo Totale Alberghi: ', v_CostoAlberghi, '\n',
                'Costo Totale Autobus: ', v_CostoAutobus, '\n',
                'Costo Totale Viaggio: ', v_CostoTotale, '\n',
                'Entrate Totali Partecipanti: ', v_EntratePartecipanti, '\n',
                'Bilancio: ', v_Bilancio, '\n',
                'Esito: ', v_Esito
                       );
        COMMIT;
    END IF;
END;

create procedure inserisciAlbergo(IN p_Nome varchar(200), IN p_Capienza int, IN p_CostoPN float,
                                  IN p_Referente varchar(100), IN p_Via varchar(60), IN p_Numero varchar(10),
                                  IN p_Cap varchar(20), IN p_Email varchar(100), IN p_Fax varchar(20),
                                  IN p_NomeLocalita varchar(50), IN p_Stato varchar(60))
BEGIN
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
    START TRANSACTION;

    -- Controllo se la località esiste
    IF NOT EXISTS (SELECT 1 FROM localita WHERE Nome = p_NomeLocalita AND Stato = p_Stato) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Località non esistente! Inserire prima la località.';
    ELSEIF EXISTS (SELECT 1 FROM albergo WHERE Nome = p_Nome AND Via = p_Via AND Numero = p_Numero AND Cap = p_Cap) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Albergo già esistente con lo stesso nome e indirizzo!';
    ELSE
        -- Inserisco l'albergo
        INSERT INTO albergo (
            Nome, Capienza, CostoPN, Referente, Via, Numero, Cap, Email, Fax, NomeLocalita, Stato
        ) VALUES (
                     p_Nome, p_Capienza, p_CostoPN, p_Referente, p_Via, p_Numero, p_Cap, p_Email, p_Fax, p_NomeLocalita, p_Stato
                 );
    END IF;

    COMMIT;
END;

create procedure inserisciAutobus(IN p_Targa varchar(10), IN p_Capienza int, IN p_CostoForfettario float)
BEGIN
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    -- Controllo se la targa esiste già
    IF EXISTS (SELECT 1 FROM autobus WHERE Targa = p_Targa) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Autobus già presente con questa targa!';
    ELSE
        INSERT INTO autobus (Targa, Capienza, CostoForfettario)
        VALUES (p_Targa, p_Capienza, p_CostoForfettario);
    END IF;

    COMMIT;
END;

create procedure inserisciLocalita(IN p_Nome varchar(50), IN p_Stato varchar(60), IN p_Provincia varchar(2),
                                   IN p_Regione varchar(25))
BEGIN
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
    START TRANSACTION;

    -- Controllo se la località esiste già
    IF EXISTS (SELECT 1 FROM localita WHERE Nome = p_Nome AND Stato = p_Stato) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Località già presente con questo nome e stato!';
    ELSE
        INSERT INTO localita (Nome, Stato, Provincia, Regione)
        VALUES (p_Nome, p_Stato, p_Provincia, p_Regione);
    END IF;

    COMMIT;
end;

create procedure inserisciPrenotazione(IN p_NumeroPartecipanti int, IN p_IDViaggio int,
                                       OUT p_CodiceDisdetta varchar(100))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    SET TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
    START TRANSACTION;

    -- Controllo se il viaggio esiste
    IF NOT EXISTS (SELECT 1 FROM viaggio WHERE IDViaggio = p_IDViaggio) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viaggio non trovato!';
    ELSEIF p_NumeroPartecipanti <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Il numero di partecipanti deve essere maggiore di zero!';
    ELSE
        -- Genero un codice di disdetta unico
        SET p_CodiceDisdetta = CONCAT('DIS', LPAD(FLOOR(RAND() * 1000000), 6, '0'));
        -- Controllo se il codice di disdetta esiste già
        WHILE EXISTS (SELECT 1 FROM prenotazioni WHERE CodiceDisdetta = p_CodiceDisdetta) DO
                SET p_CodiceDisdetta = CONCAT('DIS', LPAD(FLOOR(RAND() * 1000000), 6, '0'));
            END WHILE;

        -- Inserisco la prenotazione
        INSERT INTO prenotazioni (NumeroPartecipanti, CodiceDisdetta, CodiceViaggio)
        VALUES (p_NumeroPartecipanti, p_CodiceDisdetta, p_IDViaggio);
        COMMIT;
    END IF;
end;

create procedure login(IN p_email varchar(100), IN p_password varchar(100), OUT p_ruolo enum ('AM', 'AV'))
BEGIN
    DECLARE v_count INT DEFAULT 0;

    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    -- Controllo se l'utente esiste e recupero il ruolo
    SELECT COUNT(*), Ruolo INTO v_count, p_ruolo
    FROM user
    WHERE Email = p_email AND Password = p_password;

    IF v_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Email o password errati!';
    ELSE
        COMMIT;
    END IF;
end;

grant execute on procedure login to Agente;

grant execute on procedure login to Segreteria;

create procedure statoVIaggio(IN codiceViaggio int, OUT stato varchar(20))
BEGIN
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    SELECT Stato
    INTO stato
    FROM viaggio
    WHERE IDViaggio = codiceViaggio;

    IF stato IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viaggio non trovato';
    END IF;
    COMMIT;
END;

create procedure verificaEsistenzaViaggio(IN codiceViaggio int, OUT esiste tinyint(1), OUT Stato varchar(20))
BEGIN
    DECLARE var_count INT;
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    SELECT COUNT(*), viaggio.Stato INTO var_count, Stato
    FROM viaggio
    WHERE IDViaggio = codiceViaggio;

    SET esiste = (var_count > 0);
    IF NOT esiste THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viaggio non trovato';
    END IF;
    COMMIT;
END;

create procedure visualizzaAlberghiPerLocalita(IN p_idViaggio int, IN p_nomeLocalita varchar(50),
                                               IN p_stato varchar(60))
BEGIN
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    SELECT a.CodiceAlbergo, a.Nome, a.Capienza, a.CostoPN, a.Referente, a.Via, a.Numero, a.Cap, a.Email, a.Fax, t.NomeLocalita, t.Stato
    FROM albergo a
             JOIN tappa t ON a.NomeLocalita = t.NomeLocalita AND a.Stato = t.Stato
             JOIN viaggio v ON t.IDItinerario = v.IDItinerario
    WHERE v.IDViaggio = p_idViaggio
      AND a.NomeLocalita = p_nomeLocalita
      AND a.Stato = p_stato and Capienza > (SELECT IFNULL(SUM(p.NumeroPartecipanti),0)
                                            FROM prenotazioni p
                                            WHERE p.CodiceViaggio = v.IDViaggio);

END;

create procedure visualizzaAutobusDisponibili(IN p_idViaggio int)
BEGIN
    DECLARE v_dataPartenza DATE;
    DECLARE v_dataRientro DATE;

    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    -- Verifico che il viaggio esista
    IF NOT EXISTS (SELECT 1 FROM viaggio WHERE IDViaggio = p_idViaggio) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Il viaggio richiesto non esiste.';
    END IF;

    -- Prendi le date del viaggio richiesto
    SELECT DataPartenza, DataRientro
    INTO v_dataPartenza, v_dataRientro
    FROM viaggio
    WHERE IDViaggio = p_idViaggio;

    -- Seleziona autobus non impegnati in altri viaggi nelle stesse date
    SELECT a.Targa, a.CostoForfettario, a.Capienza
    FROM autobus a
    WHERE a.Targa NOT IN (
        SELECT av.Targa
        FROM autobusviaggio av
                 JOIN viaggio v ON av.CodiceViaggio = v.IDViaggio
        WHERE (v.DataPartenza <= v_dataRientro AND v.DataRientro >= v_dataPartenza)
    );
    COMMIT;
end;

create procedure visualizzaItinerariDisponibili()
BEGIN
    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    SELECT itinerario.IDItinerario, itinerario.NomeItinerario, itinerario.Costo, t.NomeLocalita,
           t.Stato, t.Giorni,t.Ordine
    From itinerario join agenziadiviaggi.tappa t on itinerario.IDItinerario = t.IDItinerario
    ORDER BY itinerario.IDItinerario, t.Ordine;
    COMMIT;
END;

create procedure visualizzaItinerarioConTappe(IN idViaggio int)
BEGIN
    DECLARE var_count INT;
    DECLARE var_idItinerario INT;
    -- Controllo se il viaggio esiste

    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    SELECT COUNT(*) INTO var_count
    FROM agenziadiviaggi.viaggio
    WHERE IDViaggio = idViaggio;
    IF var_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Viaggio non trovato';
    END IF;

    SELECT  i.IDItinerario, Durata, Costo, NomeItinerario, NomeLocalita, t.Stato, Ordine, Giorni
    from viaggio join agenziadiviaggi.itinerario i on i.IDItinerario = viaggio.IDItinerario join agenziadiviaggi.tappa t on i.IDItinerario = t.IDItinerario
    where viaggio.IDViaggio = idViaggio;
    COMMIT;
end;

create procedure visualizzaLocalitaDisponibili()
BEGIN

    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    SELECT Nome, Stato, Regione, Provincia
    FROM agenziadiviaggi.localita
    ORDER BY Nome;
    COMMIT;
END;

create procedure visualizzaViaggiDisponibili()
BEGIN

    -- Dichiaro il gestore di eccezioni per la transazione
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- Imposto il livello di isolamento della transazione
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED ;
    START TRANSACTION;

    -- Essendo solo ua SELECT, non inserisco un livello di isolamento
    SELECT
        v.IDViaggio,
        i.NomeItinerario,
        v.DataPartenza,
        v.Stato,
        t.Ordine,
        t.NomeLocalita,
        t.Stato AS StatoLocalita,
        t.Giorni
    FROM viaggio v
             JOIN itinerario i ON v.IDItinerario = i.IDItinerario
             JOIN tappa t ON t.IDItinerario = i.IDItinerario
    ORDER BY v.IDViaggio, t.Ordine;
    COMMIT;
END;

-- Eventi --
create event aggiorna_viaggi_confermati on schedule
    every '1' DAY
        starts '2025-08-13 17:36:33'
    enable
    do
    UPDATE viaggio
    SET Stato = 'INCORSO'
    WHERE Stato = 'PROGRAMMATO'
      AND DATEDIFF(DataPartenza, CURDATE()) = 20;

create event aggiorna_viaggi_terminati on schedule
    every '1' DAY
        starts '2025-08-13 17:36:33'
    enable
    do
    UPDATE viaggio
    SET Stato = 'TERMINATO'
    WHERE Stato = 'INCORSO'
      AND DATEDIFF(DataPartenza, CURDATE()) = -1;

create event elimina_viaggi_vecchi on schedule
    every '1' DAY
        starts '2025-08-23 15:55:26'
    enable
    do
    DELETE FROM viaggio
    WHERE DataRientro < DATE_SUB(CURDATE(), INTERVAL 5 YEAR);

-- Users --

create user Segreteria identified  by 'Segreteria';
grant execute on procedure agenziadiviaggi.creaViaggio to Segreteria;
grant execute on procedure agenziadiviaggi.inserisciLocalita to Segreteria;
grant execute on procedure agenziadiviaggi.inserisciAutobus to Segreteria;
grant execute on procedure agenziadiviaggi.inserisciAlbergo to Segreteria;
grant execute on procedure agenziadiviaggi.creaItinerario to Segreteria;
grant execute on procedure agenziadiviaggi.aggiungiTappaItinerario to Segreteria;
grant execute on procedure agenziadiviaggi.associaPernottamento to Segreteria;
grant execute on procedure agenziadiviaggi.login to Segreteria;
grant execute on procedure agenziadiviaggi.associaAutobusViaggio to Segreteria;
grant execute on procedure agenziadiviaggi.generaReportViaggio to Segreteria;
grant execute on procedure agenziadiviaggi.visualizzaViaggiDisponibili to Segreteria;
grant execute on procedure agenziadiviaggi.visualizzaItinerariDisponibili to Segreteria;
grant execute on procedure agenziadiviaggi.visualizzaLocalitaDisponibili to Segreteria;
grant execute on procedure agenziadiviaggi.visualizzaAutobusDisponibili to Segreteria;
grant execute on procedure agenziadiviaggi.visualizzaItinerarioConTappe to Segreteria;
grant execute on procedure agenziadiviaggi.visualizzaAlberghiPerLocalita to Segreteria;
grant execute on procedure agenziadiviaggi.verificaEsistenzaViaggio to Segreteria;


create user Agente identified by 'Agente';
grant execute on procedure agenziadiviaggi.cancellazionePrenotazione to Agente;
grant execute on procedure agenziadiviaggi.inserisciPrenotazione to Agente;
grant execute on procedure agenziadiviaggi.login to Agente;
grant execute on procedure agenziadiviaggi.visualizzaViaggiDisponibili to Agente;