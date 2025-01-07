-- Creazione schema prenotazione_service
CREATE SCHEMA IF NOT EXISTS prenotazione_service;

-- Rimozione delle tabelle esistenti (se presenti)
DROP TABLE IF EXISTS prenotazione_service.prenotazione;
DROP TABLE IF EXISTS prenotazione_service.preventivo;

DROP SEQUENCE IF EXISTS prenotazione_service.seq_preventivo;
DROP SEQUENCE IF EXISTS prenotazione_service.seq_prenotazione;


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
    group_id VARCHAR(50) NOT NULL, -- Group ID per la prenotazione
    lista_id_fascia_eta INTEGER ARRAY NOT NULL, -- Array di ID fascia età
    check_in TIMESTAMP NOT NULL, -- Data di check-in
    check_out TIMESTAMP NOT NULL, -- Data di check-out
    prezzo_totale DECIMAL(10,2) NOT NULL, -- ID del prezzo della camera
    numero_camera VARCHAR(5)
);


-- Creazione tabella PRENOTAZIONE (con il campo id_preventivo)
CREATE TABLE IF NOT EXISTS prenotazione_service.prenotazione (
    id INTEGER NOT NULL DEFAULT NEXT VALUE FOR prenotazione_service.seq_prenotazione PRIMARY KEY,
    codice_prenotazione VARCHAR(100) NOT NULL, -- Codice della prenotazione
    id_utente INTEGER NOT NULL, -- ID dell'utente che ha effettuato la prenotazione
    id_metodo_pagamento INTEGER NOT NULL, -- ID del metodo di pagamento
    id_preventivo INTEGER, -- Riferimento all'ID del preventivo
    FOREIGN KEY (id_preventivo) REFERENCES prenotazione_service.preventivo(id) -- Aggiunta della chiave esterna
);


-- Inserimento di un preventivo fittizio
INSERT INTO prenotazione_service.preventivo (
    id_tipo_camera,
    lista_id_fascia_eta,
    check_in,
    check_out,
    id_prezzo_camera
)
VALUES (
    1,  -- Esempio di ID tipo di camera
    ARRAY[1, 2, 3],  -- Esempio di array di ID per la fascia età
    '2024-12-25 14:00:00',  -- Data di check-in (formato TIMESTAMP)
    '2024-12-30 10:00:00',  -- Data di check-out (formato TIMESTAMP)
    100  -- Esempio di ID prezzo della camera
);
