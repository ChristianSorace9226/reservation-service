-- Inserimento delle percentuali di maggiorazione (per le stagioni)
INSERT INTO PRENOTAZIONE_SERVICE.MAGGIORAZIONE (ID, PERCENTUALE_MAGGIORAZIONE)
VALUES (1, 30), (2, 0), (3, -20);

-- Inserimento delle stagioni con i periodi associati e la maggiorazione
INSERT INTO PRENOTAZIONE_SERVICE.STAGIONE (ID, NOME, GIORNO_INIZIO, GIORNO_FINE, MAGGIORAZIONE_ID)
VALUES
    (1, 'Alta', '2025-05-01', '2025-08-31', 1),
    (2, 'Media', '2025-09-01', '2025-01-31', 2),
    (3, 'Bassa', '2025-02-01', '2025-04-30', 3);

INSERT INTO PRENOTAZIONE_SERVICE.METODO_PAGAMENTO (ID, METODO_PAGAMENTO)
VALUES
    (1, 'Unica soluzione'),
    (2, 'Anticipo');