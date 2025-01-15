-- Creazione schema prenotazione_service
CREATE SCHEMA IF NOT EXISTS prenotazione_service;

-- Rimozione delle tabelle esistenti (se presenti)
DROP TABLE IF EXISTS prenotazione_service.prenotazione;
DROP TABLE IF EXISTS prenotazione_service.stagione;
DROP TABLE IF EXISTS prenotazione_service.maggiorazione;

DROP SEQUENCE IF EXISTS prenotazione_service.seq_prenotazione;

CREATE SEQUENCE IF NOT EXISTS prenotazione_service.seq_prenotazione
START WITH 1
INCREMENT BY 1
MAXVALUE 9999
NOCACHE
NOCYCLE;

-- Creazione tabella PRENOTAZIONE (con il campo id_preventivo)
CREATE TABLE IF NOT EXISTS prenotazione_service.prenotazione (
    id INTEGER NOT NULL DEFAULT NEXT VALUE FOR prenotazione_service.seq_prenotazione PRIMARY KEY,
    id_utente INTEGER NOT NULL, -- ID dell'utente che ha effettuato la prenotazione
    numero_camera VARCHAR(5),
    id_tipo_camera INTEGER NOT NULL, -- ID del tipo di camera
    check_in TIMESTAMP NOT NULL, -- Data di check-in
    check_out TIMESTAMP NOT NULL, -- Data di check-out
    lista_eta INTEGER ARRAY NOT NULL, -- Array di età
    data_creazione TIMESTAMP NOT NULL, -- Data creazione prenotazione
    data_annullamento TIMESTAMP, -- Data annullamento prenotazione
    motivo_annullamento VARCHAR(50), -- Motivo annullamento della prenotazione
    id_metodo_pagamento INTEGER NOT NULL, -- ID del metodo di pagamento
    prezzo_totale DECIMAL(10,2) NOT NULL, -- ID del prezzo della camera
    prezzi_a_persona DECIMAL(10,2) ARRAY NOT NULL, -- Prezzi per ogni età
    codice_prenotazione VARCHAR(100) NOT NULL, -- Codice della prenotazione
    group_id VARCHAR(50) NOT NULL, -- Group ID per la prenotazione
    CONSTRAINT uq_cod_prenotazione UNIQUE (codice_prenotazione)
);

-- Creazione della tabella MAGGIORAZIONE
CREATE TABLE prenotazione_service.maggiorazione (
    id INTEGER PRIMARY KEY,
    percentuale_maggiorazione INT NOT NULL
);

-- Creazione della tabella STAGIONE con i periodi di inizio e fine
CREATE TABLE prenotazione_service.stagione (
    id INTEGER PRIMARY KEY,
    nome VARCHAR(30) NOT NULL,
    giorno_inizio DATE NOT NULL,
    giorno_fine DATE NOT NULL,
    maggiorazione_id INTEGER,
    FOREIGN KEY (maggiorazione_id) REFERENCES prenotazione_service.maggiorazione(id)
);