package it.nesea.prenotazione_service.util;

import it.nesea.albergo.common_lib.dto.request.CheckDateStart;
import it.nesea.albergo.common_lib.exception.BadRequestException;
import it.nesea.albergo.common_lib.exception.NotFoundException;
import it.nesea.prenotazione_service.controller.feign.HotelExternalController;
import it.nesea.prenotazione_service.controller.feign.UserExternalController;
import it.nesea.prenotazione_service.dto.request.PreventivoRequest;
import it.nesea.prenotazione_service.model.MetodoPagamentoEntity;
import it.nesea.prenotazione_service.model.StagioneEntity;
import it.nesea.prenotazione_service.model.repository.MetodoPagamentoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class Util {

    private final EntityManager entityManager;
    private final UserExternalController userExternalController;
    private final HotelExternalController hotelExternalController;
    private final MetodoPagamentoRepository metodoPagamentoRepository;

    public static <T, R> List<R> filtraRichiesta(T request, Class<R> entityClass, EntityManager entityManager) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> cq = cb.createQuery(entityClass);
        Root<R> root = cq.from(entityClass);

        List<Predicate> predicates = new ArrayList<>();
        Field[] requestFields = request.getClass().getDeclaredFields();

        for (Field requestField : requestFields) {
            try {
                // Ignora campi statici o non rilevanti
                if (java.lang.reflect.Modifier.isStatic(requestField.getModifiers()) ||
                        java.lang.reflect.Modifier.isTransient(requestField.getModifiers())) {
                    continue;
                }

                // Rendi il campo della richiesta accessibile
                requestField.setAccessible(true);
                Object requestValue = requestField.get(request);

                if (requestValue != null) {
                    // Assumi che i nomi dei campi di request e entity corrispondano
                    Path<Object> entityFieldPath = root.get(requestField.getName());
                    Predicate predicate = cb.equal(entityFieldPath, requestValue);
                    predicates.add(predicate);
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        // Combina tutti i predicati
        cq.where(predicates.toArray(new Predicate[0]));

        // Esegui la query
        return entityManager.createQuery(cq).getResultList();
    }


    public String generaCodicePrenotazione() {
        return UUID.randomUUID().toString();
    }

    public String generaGroupId() {
        return generaCodicePrenotazione().substring(0, 4);
    }

    public void isDateValid(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkOut.isBefore(checkIn)) {
            throw new BadRequestException("Data check out antecedente a quella di check in");
        }
        if (checkIn.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Data check in non può essere nel futuro");
        }
    }

    public long calcolaNumeroGiorni(LocalDateTime dataInizio, LocalDateTime dataFine) {
        if (dataInizio == null || dataFine == null) {
            throw new IllegalArgumentException("Le date di checkIn e checkOut non possono essere null");
        }
        return ChronoUnit.DAYS.between(dataInizio, dataFine);
    }

    public void checkUtente(Integer idUtente) {
        if (!userExternalController.checkUtente(idUtente).getBody().getResponse()) {
            log.error("Utente {} non valido", idUtente);
            throw new NotFoundException("Utente non valido");
        }
    }

    public MetodoPagamentoEntity checkMetodoPagamento(Integer idMetodoPagamento) {
        return metodoPagamentoRepository.findById(idMetodoPagamento)
                .orElseThrow(() -> new NotFoundException("Metodo di pagamento non trovato"));
    }

    public void checkDisponibilita(String numeroCamera, LocalDateTime checkIn) {
        if (!hotelExternalController.checkDisponibilita(new CheckDateStart(numeroCamera, checkIn)).getBody().getResponse()) {
            log.error("Camera non ancora disponibile");
            throw new BadRequestException("Camera non ancora disponibile");
        }
    }

    public Integer getPercentualeMaggiorazione(LocalDateTime checkIn, LocalDateTime checkOut, List<StagioneEntity> stagioni) {

        int meseCheckIn = checkIn.getMonthValue();
        int giornoCheckIn = checkIn.getDayOfMonth();

        int meseCheckOut = checkOut.getMonthValue();
        int giornoCheckOut = checkOut.getDayOfMonth();

        Integer percentualeMaggiorazione = null;

        for (StagioneEntity stagione : stagioni) {
            int meseInizio = stagione.getGiornoInizio().getMonthValue();
            int giornoInizio = stagione.getGiornoInizio().getDayOfMonth();

            int meseFine = stagione.getGiornoFine().getMonthValue();
            int giornoFine = stagione.getGiornoFine().getDayOfMonth();

            boolean stagioneCoincidente = false;
            if (meseInizio < meseFine || (meseInizio == meseFine && giornoInizio <= giornoFine)) {
                if ((meseCheckIn > meseInizio || (meseCheckIn == meseInizio && giornoCheckIn >= giornoInizio)) &&
                        (meseCheckOut < meseFine || (meseCheckOut == meseFine && giornoCheckOut <= giornoFine))) {
                    stagioneCoincidente = true;
                }
            } else {
                if ((meseCheckIn > meseInizio || (meseCheckIn == meseInizio && giornoCheckIn >= giornoInizio)) ||
                        (meseCheckOut < meseFine || (meseCheckOut == meseFine && giornoCheckOut <= giornoFine))) {
                    stagioneCoincidente = true;
                }
            }

            if (stagioneCoincidente) {
                percentualeMaggiorazione = stagione.getMaggiorazione().getPercentualeMaggiorazione();
                break;
            }
        }

        if (percentualeMaggiorazione == null) {
            throw new NotFoundException("Nessuna stagione trovata per il periodo richiesto.");
        }
        return percentualeMaggiorazione;
    }

    public PreventivoRequest calcolaPrezzoFinale(PreventivoRequest request) {
        LocalDateTime checkIn = request.getCheckIn();
        LocalDateTime checkOut = request.getCheckOut();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StagioneEntity> percentualeMaggiorazioneQuery = cb.createQuery(StagioneEntity.class);
        Root<StagioneEntity> stagioneRoot = percentualeMaggiorazioneQuery.from(StagioneEntity.class);
        percentualeMaggiorazioneQuery.select(stagioneRoot);

        List<StagioneEntity> stagioni = entityManager.createQuery(percentualeMaggiorazioneQuery).getResultList();

        int percentualeMaggiorazione = getPercentualeMaggiorazione(checkIn, checkOut, stagioni);

        List<BigDecimal> prezziAPersona = request.getPrezzarioCamera().getPrezziAPersona();
        prezziAPersona.replaceAll(aPersona -> aPersona.multiply(BigDecimal.valueOf(1 + (percentualeMaggiorazione / 100.0))));

        request.getPrezzarioCamera().setPrezziAPersona(prezziAPersona);

        BigDecimal prezzoNuoviOccupanti = request.getPrezzarioCamera().getPrezzoTotale()
                .multiply(BigDecimal.valueOf(calcolaNumeroGiorni(checkIn, checkOut)));

        BigDecimal prezzoNuoviOccupantiConMaggiorazione = prezzoNuoviOccupanti
                .multiply(BigDecimal.valueOf(1 + (percentualeMaggiorazione / 100.0)));

        request.getPrezzarioCamera().setPrezzoTotale(prezzoNuoviOccupantiConMaggiorazione);
        return request;
    }
}
