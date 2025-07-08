package com.gaegxh.firebirdtask2.service.Api.Impl;

import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.gaegxh.firebirdtask2.service.Api.BestTicketSelector;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Service
public class BestTicketSelectorImpl implements BestTicketSelector {

    private static final long MAX_DEPARTURE_WINDOW_MINUTES = 30;


    @Override
    public boolean isBetterTicket(TrainInfo candidate, TrainInfo currentBest, String criteria) {
        if (candidate == null) return false;
        if (currentBest == null) return true;

        if (criteria != null && criteria.startsWith("CLOSEST_TO_TIME")) {
            LocalTime targetTime = parseTimeFromCriteria(criteria);
            if (targetTime == null) return false;

            try {
                LocalTime candidateTime = LocalTime.parse(candidate.getDepartureTime());
                LocalTime bestTime = LocalTime.parse(currentBest.getDepartureTime());

                long candidateDiff = Math.abs(ChronoUnit.MINUTES.between(candidateTime, targetTime));
                long bestDiff = Math.abs(ChronoUnit.MINUTES.between(bestTime, targetTime));

                return candidateDiff < bestDiff;
            } catch (Exception e) {
                return false;
            }
        }


        if ("EARLIEST_DEPARTURE".equalsIgnoreCase(criteria)) {
            try {
                LocalTime candidateTime = LocalTime.parse(candidate.getDepartureTime());
                LocalTime bestTime = LocalTime.parse(currentBest.getDepartureTime());
                return candidateTime.isBefore(bestTime);
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean isAcceptableTicket(TrainInfo ticket, String criteria) {
        if (criteria != null && criteria.startsWith("CLOSEST_TO_TIME")) {
            LocalTime targetTime = parseTimeFromCriteria(criteria);
            if (targetTime == null) return true;

            try {
                LocalTime departure = LocalTime.parse(ticket.getDepartureTime());
                long diff = Math.abs(ChronoUnit.MINUTES.between(departure, targetTime));
                return diff <= 50;
            } catch (Exception e) {
                return true;
            }
        }

        if ("EARLIEST_DEPARTURE".equalsIgnoreCase(criteria)) {
            try {
                LocalTime departure = LocalTime.parse(ticket.getDepartureTime());
                long diff = ChronoUnit.MINUTES.between(LocalTime.now(), departure);
                return diff >= 0 && diff <= 120;
            } catch (Exception e) {
                return true;
            }
        }

        return true;
    }


    private LocalTime parseTimeFromCriteria(String criteria) {
        try {
            String[] parts = criteria.split(":");
            if (parts.length == 2) {
                return LocalTime.parse(parts[1].trim());
            }
        } catch (DateTimeParseException e) {
            // Игнорируем ошибку парсинга
        }
        return null;
    }

}