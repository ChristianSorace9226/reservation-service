-- Creazione schema prenotazione_service
CREATE SCHEMA IF NOT EXISTS prenotazione_service;

-- Rimozione delle tabelle esistenti (se presenti)
DROP TABLE IF EXISTS prenotazione_service.preventivo;
DROP TABLE IF EXISTS prenotazione_service.prenotazione;

-- Creazione sequenze per l'auto incremento degli ID
CREATE SEQUENCE IF NOT EXISTS prenotazione_service.seq_preventivo
START WITH 1
INCREMENT BY 1
MAXVALUE 9999
NOCACHE
NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS prenotazione_service.seq_prenotazione
START WITH 1
INCREMENT BY 1
MAXVALUE 9999
NOCACHE
NOCYCLE;

-- Creazione tabella PREVENTIVO
CREATE TABLE IF NOT EXISTS prenotazione_service.preventivo (
    id INTEGER NOT NULL DEFAULT NEXT VALUE FOR prenotazione_service.seq_preventivo PRIMARY KEY,
    id_tipo_camera INTEGER NOT NULL, -- ID del tipo di camera
    lista_id_fascia_eta INTEGER ARRAY NOT NULL, -- Array di ID fascia età
    check_in TIMESTAMP NOT NULL, -- Data di check-in
    check_out TIMESTAMP NOT NULL, -- Data di check-out
    id_prezzo_camera INTEGER NOT NULL -- ID del prezzo della camera
);

-- Creazione tabella PRENOTAZIONE
CREATE TABLE IF NOT EXISTS prenotazione_service.prenotazione (
    id INTEGER NOT NULL DEFAULT NEXT VALUE FOR prenotazione_service.seq_prenotazione PRIMARY KEY,
    codice_prenotazione VARCHAR(16) NOT NULL, -- Codice della prenotazione
    fascia_eta INTEGER ARRAY NOT NULL, -- Array di ID fascia età
    id_tipo_stanza INTEGER NOT NULL, -- ID del tipo di stanza
    check_in TIMESTAMP NOT NULL, -- Data di check-in
    check_out TIMESTAMP NOT NULL, -- Data di check-out
    id_utente INTEGER NOT NULL, -- ID dell'utente che ha effettuato la prenotazione
    group_id VARCHAR(50) NOT NULL, -- Group ID per la prenotazione
    prezzo_camera DECIMAL(10, 2) NOT NULL, -- Prezzo della camera
    id_metodo_pagamento INTEGER NOT NULL -- ID del metodo di pagamento
);
