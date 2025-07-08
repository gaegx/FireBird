package com.gaegxh.firebirdtask2.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class BookingSummary {

    private String bookingId;

    private String fromStation;
    private String toStation;

    private String departureTime;

    private String trainClass;

    private double price;
    private String currency;

    private List<Passenger> passengers;

    private String paymentStatus;

    public String[] toCsvRow() {
        String passengerNames = passengers != null
                ? passengers.stream()
                .map(Passenger::getFull_name)
                .collect(Collectors.joining(" | "))
                : "";

        return new String[] {
                bookingId,
                fromStation,
                toStation,
                departureTime,
                trainClass,
                String.valueOf(price),
                currency,
                passengerNames,
                paymentStatus
        };
    }
}
