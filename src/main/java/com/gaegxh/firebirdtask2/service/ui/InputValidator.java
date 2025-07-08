package com.gaegxh.firebirdtask2.service.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class InputValidator {
    private static final List<String> ALLOWED_ROUTES = Arrays.asList(
            "PARIS-LILLE", "LILLE-PARIS",
            "LISBON-PORTO", "PORTO-LISBON"
    );

    private static final DateTimeFormatter INPUT_DATE_FORMAT_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter INPUT_DATE_FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static boolean validateCities(String departure, String arrival) {
        if (departure == null || arrival == null || departure.trim().isEmpty() || arrival.trim().isEmpty()) {
            return false;
        }

        String route = (departure.trim().toUpperCase() + "-" + arrival.trim().toUpperCase());
        return ALLOWED_ROUTES.contains(route);
    }

    public static String validateAndFormatDob(String dob) {
        if (dob == null || dob.isEmpty()) return null;

        // Форматы, которые мы поддерживаем
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate date = null;
        try {
            date = LocalDate.parse(dob, formatter1);
        } catch (DateTimeParseException ignored) {}

        if (date == null) {
            try {
                date = LocalDate.parse(dob, formatter2);
            } catch (DateTimeParseException ignored) {}
        }

        if (date == null) {
            return null;
        }

        // Возвращаем в формате yyyy-MM-dd (ISO)
        return date.format(formatter1);
    }

    public static String validateAndFormatDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            LocalDate date = null;
            String trimmedDate = dateStr.trim();

            // Try parsing as DD.MM.YYYY
            try {
                date = LocalDate.parse(trimmedDate, INPUT_DATE_FORMAT_DDMMYYYY);
            } catch (DateTimeParseException e) {
                // Try parsing as YYYY-MM-DD
                try {
                    date = LocalDate.parse(trimmedDate, INPUT_DATE_FORMAT_YYYYMMDD);
                } catch (DateTimeParseException ignored) {
                    return null;
                }
            }

            LocalDate today = LocalDate.now();
            if (date.isBefore(today) || date.isAfter(today.plusYears(1))) {
                return null;
            }

            return date.format(API_DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateTime(String time) {
        try {
            LocalTime.parse(time); // формат HH:mm
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$");
    }

    public static boolean validateDob(String dob) {
        try {
            LocalDate.parse(dob); // формат yyyy-MM-dd
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}