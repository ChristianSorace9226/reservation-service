package it.nesea.prenotazione_service.service;

import it.nesea.albergo.common_lib.dto.request.RichiediRimborso;
import it.nesea.albergo.common_lib.dto.response.RimborsoResponse;
import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.controller.feign.HotelExternalController;
import it.nesea.prenotazione_service.controller.feign.PagamentoExternalController;
import it.nesea.prenotazione_service.dto.request.AnnullaPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.ModificaPrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PrenotazioneRequest;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.dto.response.AnnullaPrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PrenotazioneResponse;
import it.nesea.prenotazione_service.dto.response.PreventivoResponse;
import it.nesea.prenotazione_service.mapper.PrenotazioneMapper;
import it.nesea.prenotazione_service.mapper.PreventivoMapper;
import it.nesea.prenotazione_service.model.MetodoPagamentoEntity;
import it.nesea.prenotazione_service.model.Prenotazione;
import it.nesea.prenotazione_service.model.repository.MetodoPagamentoRepository;
import it.nesea.prenotazione_service.model.repository.PrenotazioneRepository;
import it.nesea.prenotazione_service.util.Util;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class PrenotazioneServiceImpl implements PrenotazioneService {

    private final Util util;
    private final HotelExternalController hotelExternalController;
    private final PagamentoExternalController pagamentoExternalController;
    private final EntityManager entityManager;
    private final PreventivoMapper preventivoMapper;
    private final PrenotazioneMapper prenotazioneMapper;
    private final PrenotazioneRepository prenotazioneRepository;
    private final MetodoPagamentoRepository metodoPagamentoRepository;


    @Transactional
    @Override
    public PrenotazioneResponse prenotazione(PrenotazioneRequest request) {
        log.debug("Oggetto request in input: [{}]", request);

        String numeroCamera = request.getPrezzarioCamera().getNumeroCamera();
        LocalDateTime checkIn = request.getCheckIn();
        LocalDateTime checkOut = request.getCheckOut();

        List<Prenotazione> prenotazioniEsistenti = prenotazioneRepository.findByNumeroCamera(numeroCamera);

        for (Prenotazione prenotazioneTrovata : prenotazioniEsistenti) {
            boolean isOverlapping = checkIn.isBefore(prenotazioneTrovata.getCheckOut()) &&
                    checkOut.isAfter(prenotazioneTrovata.getCheckIn());

            if (isOverlapping) {
                if (prenotazioneTrovata.getDataAnnullamento() != null) {
                    continue;
                }

                if (request.getGroupId() != null && !request.getGroupId().equals(prenotazioneTrovata.getGroupId())) {
                    log.error("La camera è già prenotata con un groupId diverso");
                    throw new BadRequestException("La camera è già prenotata con un groupId diverso per le date richieste");
                }

                if (request.getGroupId() != null) {
                    int capienzaCamera = prenotazioneTrovata.getIdTipoCamera();
                    int personePrenotate = prenotazioneTrovata.getListaEta().size();

                    personePrenotate += request.getListaEta().size();

                    if (personePrenotate > capienzaCamera) {
                        log.error("Il numero di persone nel gruppo supera la capienza della camera");
                        throw new BadRequestException("Il numero di persone nel gruppo supera la capienza della camera");
                    }
                } else {
                    log.error("La camera è già prenotata per le date richieste");
                    throw new BadRequestException("La camera è già prenotata per le date richieste");
                }
            }
        }

        util.isDateValid(checkIn, checkOut);
        util.checkDisponibilita(numeroCamera, checkIn);
        util.checkUtente(request.getIdUtente());
        MetodoPagamentoEntity metodoPagamento = util.checkMetodoPagamento(request.getIdMetodoPagamento());

        Prenotazione prenotazione = new Prenotazione();

        if (request.getGroupId() != null) {
            List<Prenotazione> prenotazioniEsistentiGroup = prenotazioneRepository.findByGroupId(request.getGroupId());
            if (prenotazioniEsistentiGroup.isEmpty()) {
                log.error("Prenotazioni con groupId {} non trovate", request.getGroupId());
                throw new NotFoundException("Prenotazioni con groupId non trovate");
            }

            if (checkOut.isAfter(prenotazioniEsistentiGroup.getFirst().getCheckOut())) {
                log.error("La data di check out non può essere posteriore a quella della prenotazione esistente");
                throw new BadRequestException("La data di check out non può essere posteriore a quella della prenotazione esistente");
            }

            int capienzaCamera = prenotazioniEsistentiGroup.getFirst().getIdTipoCamera();

            int personePrenotate = 0;
            for (Prenotazione prenotazioneEsistente : prenotazioniEsistentiGroup) {
                personePrenotate += prenotazioneEsistente.getListaEta().size();
            }

            if (personePrenotate + request.getListaEta().size() > capienzaCamera) {
                log.error("Il numero di persone da unire al gruppo eccede la capienza della camera");
                throw new BadRequestException("Il numero di persone da unire al gruppo eccede la capienza della camera");
            }

            List<Integer> etaEsistenti = new ArrayList<>();
            for (Prenotazione prenotazioneEsistente : prenotazioniEsistentiGroup) {
                etaEsistenti.addAll(prenotazioneEsistente.getListaEta());
            }
            List<Integer> listaEta = request.getListaEta();
            etaEsistenti.addAll(listaEta);
            request.setListaEta(etaEsistenti);

            PreventivoRequest preventivoRequest = util.calcolaPrezzoFinale(request);
            request.setPrezzarioCamera(preventivoRequest.getPrezzarioCamera());

            List<BigDecimal> listaPrezzi = new ArrayList<>();
            BigDecimal prezzoParziale = BigDecimal.ZERO;
            for (int i = listaEta.size() - 1; i >= 0; i--) {
                listaPrezzi.add(request.getPrezzarioCamera().getPrezziAPersona().get(i));
                prezzoParziale = prezzoParziale.add(request.getPrezzarioCamera().getPrezziAPersona().get(i));
            }

            BigDecimal giorniPermanenza = BigDecimal.valueOf(util.calcolaNumeroGiorni(request.getCheckIn(), request.getCheckOut()));
            prenotazione = prenotazioneMapper.fromPrenotazioneRequestToPrenotazione(request);
            prenotazione.setPrezziAPersona(listaPrezzi);
            prenotazione.setPrezzoTotale(prezzoParziale.multiply(giorniPermanenza));

            prenotazione.setListaEta(listaEta);
            prenotazione.setCodicePrenotazione(util.generaCodicePrenotazione());

            prenotazione.setGroupId(request.getGroupId());
            prenotazione.setDataCreazione(LocalDateTime.now());

            prenotazioneRepository.save(prenotazione);
            return prenotazioneMapper.fromPrenotazioneToPrenotazioneResponse(prenotazione);
        }

        PreventivoRequest preventivoRequest = util.calcolaPrezzoFinale(request);
        request.setPrezzarioCamera(preventivoRequest.getPrezzarioCamera());
        prenotazione = prenotazioneMapper.fromPrenotazioneRequestToPrenotazione(request);
        prenotazione.setCodicePrenotazione(util.generaCodicePrenotazione());
        prenotazione.setDataCreazione(LocalDateTime.now());
        prenotazione.setGroupId(util.generaGroupId());
        prenotazione.setMetodoPagamento(metodoPagamento);

        prenotazioneRepository.save(prenotazione);
        return prenotazioneMapper.fromPrenotazioneToPrenotazioneResponse(prenotazione);
    }


    @Override
    public PreventivoResponse richiediPreventivo(PreventivoRequest request) {
        util.isDateValid(request.getCheckIn(), request.getCheckOut());
        util.checkDisponibilita(request.getPrezzarioCamera().getNumeroCamera(), request.getCheckIn());
        request = util.calcolaPrezzoFinale(request);

        return preventivoMapper.fromPrezzoCameraDTOToPreventivoResponse(request.getPrezzarioCamera());
    }

    @Transactional
    @Override
    public PrenotazioneResponse modificaPrenotazione(ModificaPrenotazioneRequest request) {
        log.info("Oggetto request in input per la modifica: [{}]", request);

        Prenotazione prenotazione = prenotazioneRepository.findById(request.getId())
                .orElseThrow(() -> {
                    log.error("Prenotazione con ID {} non trovata", request.getId());
                    return new NotFoundException("Prenotazione non trovata");
                });

        util.isDateValid(request.getCheckIn(), request.getCheckOut());

        util.checkDisponibilita(request.getPrezzarioCamera().getNumeroCamera(), request.getCheckIn());

        prenotazione.setMetodoPagamento(util.checkMetodoPagamento(request.getIdMetodoPagamento()));
        prenotazione.setCheckIn(request.getCheckIn());
        prenotazione.setCheckOut(request.getCheckOut());
        prenotazione.setListaEta(request.getListaEta());

        PreventivoRequest preventivoRequest = util.calcolaPrezzoFinale(request);
        request.setPrezzarioCamera(preventivoRequest.getPrezzarioCamera());

        List<BigDecimal> listaPrezzi = new ArrayList<>();
        BigDecimal prezzoParziale = BigDecimal.ZERO;
        for (int i = request.getListaEta().size() - 1; i >= 0; i--) {
            listaPrezzi.add(request.getPrezzarioCamera().getPrezziAPersona().get(i));
            prezzoParziale = prezzoParziale.add(request.getPrezzarioCamera().getPrezziAPersona().get(i));
        }

        BigDecimal giorniPermanenza = BigDecimal.valueOf(util.calcolaNumeroGiorni(request.getCheckIn(), request.getCheckOut()));
        prenotazione.setPrezziAPersona(listaPrezzi);
        prenotazione.setPrezzoTotale(prezzoParziale.multiply(giorniPermanenza));

        prenotazioneRepository.save(prenotazione);
        log.info("Oggetto Prenotazione modificato salvato sul db: [{}]", prenotazione);

        return prenotazioneMapper.fromPrenotazioneToPrenotazioneResponse(prenotazione);

    }

    @Transactional
    @Override
    public AnnullaPrenotazioneResponse annullaPrenotazione(AnnullaPrenotazioneRequest request) {
        log.info("Ricevuta richiesta annullamento prenotazione [{}]", request);

        util.checkUtente(request.getIdUtente());

        Prenotazione prenotazione = prenotazioneRepository.findById(request.getIdPrenotazione())
                .orElseThrow(() -> new NotFoundException("Prenotazione non trovata"));

        int giornoCheckIn = prenotazione.getCheckIn().getDayOfYear();
        int oggi = LocalDateTime.now().getDayOfYear();
        int percentualeRimborso = 100;

        if (giornoCheckIn - oggi < 15) {
            percentualeRimborso = 50;
            if (giornoCheckIn - oggi < 5) {
                percentualeRimborso = 0;
            }
        }

        BigDecimal rimborso = prenotazione.getPrezzoTotale()
                .multiply(BigDecimal.valueOf(percentualeRimborso)
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));

        RichiediRimborso richiediRimborso = new RichiediRimborso();
        richiediRimborso.setImportoRimborso(rimborso);
        richiediRimborso.setIdPrenotazione(request.getIdPrenotazione());

        if (request.getCancellazioneLogica() == Boolean.TRUE) {
            prenotazione.setDataAnnullamento(LocalDateTime.now());
            prenotazione.setMotivoAnnullamento(request.getMotivoAnnullamento());
            prenotazioneRepository.save(prenotazione);
        } else {
            prenotazioneRepository.delete(prenotazione);
        }

        AnnullaPrenotazioneResponse response = new AnnullaPrenotazioneResponse();

        RimborsoResponse rimborsoResponse = pagamentoExternalController.richiediRimborso(richiediRimborso)
                .getBody().getResponse();

        if (!rimborsoResponse.getEsitoRimborso()) {
            log.error("Rimborso non riuscito");
            response.setMessaggio("Rimborso non riuscito");
            response.setRimborso(BigDecimal.ZERO);
            return response;
        }

        response.setMessaggio("Prenotazione annullata con successo");
        response.setRimborso(rimborsoResponse.getCreditoRimborsato());
        return response;

    }

}
