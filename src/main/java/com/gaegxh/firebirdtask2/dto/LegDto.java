package com.gaegxh.firebirdtask2.dto;

import lombok.Data;

@Data
public class LegDto {
    private String arrivalDatetime;
    private String arrivalStation;
    private String carrierUuid;
    private String coachClassUuid;
    private String departureDatetime;
    private String departureStation;
    private String fareUuid;
    private int price;
    private String trainNumber;
}