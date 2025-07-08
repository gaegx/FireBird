package com.gaegxh.firebirdtask2.mapper;

import com.gaegxh.firebirdtask2.config.RouteConfig;
import com.gaegxh.firebirdtask2.dto.LegDto;
import com.gaegxh.firebirdtask2.dto.PassengersDto;
import com.gaegxh.firebirdtask2.dto.SearchRequestDto;
import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class TrainInfoToSearchRequestMapper {

    private final Gson gson;
    @Autowired
    private RouteConfig routeConfig;

    public TrainInfoToSearchRequestMapper(Gson gson, RouteConfig routeConfig) {
        this.gson = gson;
        this.routeConfig = routeConfig;
    }
    public TrainInfoToSearchRequestMapper() {
        // Настраиваем Gson для snake_case
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public SearchRequestDto toSearchRequestDto(TrainInfo trainInfo) {
        if (trainInfo == null) {
            throw new IllegalArgumentException("TrainInfo не может быть null");
        }

        if (hasUnknownFields(trainInfo)) {
            throw new IllegalArgumentException("TrainInfo содержит поля со значением 'Unknown'");
        }

        SearchRequestDto searchRequestDto = new SearchRequestDto();
        searchRequestDto.setBaseUrl("https://rail.ninja");

        // Устанавливаем данные о пассажирах
        PassengersDto passengers = new PassengersDto();
        passengers.setAdults(1);
        passengers.setChildren(0);
        passengers.setChildrenAge(Collections.emptyList());
        searchRequestDto.setPassengers(passengers);

        // Устанавливаем searchSessionId
        String searchSessionId = trainInfo.getSearchSessionId();
        searchRequestDto.setSearchSessionId(searchSessionId != null && !searchSessionId.equals("Unknown")
                ? searchSessionId
                : "unknown_session_id");

        // Преобразуем TrainInfo в LegDto
        Map<String, LegDto> legs = new HashMap<>();
        LegDto legDto = toLegDto(trainInfo);
        legs.put("1", legDto);
        searchRequestDto.setLegs(legs);

        return searchRequestDto;
    }

    public JsonElement toJson(TrainInfo trainInfo) {
        SearchRequestDto searchRequestDto = toSearchRequestDto(trainInfo);
        try {
            return gson.toJsonTree(searchRequestDto);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при преобразовании SearchRequestDto в JSON: " + e.getMessage());
        }
    }

    private LegDto toLegDto(TrainInfo trainInfo) {
        LegDto legDto = new LegDto();
        legDto.setTrainNumber(trainInfo.getTrainNumber());


        legDto.setDepartureDatetime(formatToIso8601(trainInfo.getDepartureTime()));
        legDto.setArrivalDatetime(formatToIso8601(trainInfo.getArrivalTime()));


        legDto.setDepartureStation(trainInfo.getDepartureStationUuid());
        legDto.setArrivalStation(trainInfo.getArrivalStationUuid());

        legDto.setCarrierUuid(trainInfo.getCarrierUuid());
        legDto.setCoachClassUuid(trainInfo.getCoachClassUuid());
        legDto.setFareUuid(trainInfo.getFareUuid());
        legDto.setPrice(trainInfo.getPrice());

        return legDto;
    }

    private String formatToIso8601(String time) {
        if (time == null || time.equals("Unknown")) {
            return null;
        }
        try {

            LocalDate fixedDate = LocalDate.of(2025, 7, 19); // Фиксированная дата из JSON
            String isoDateTime = String.format("%sT%s:00+02:00", fixedDate, time);
            OffsetDateTime.parse(isoDateTime);
            return isoDateTime;
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка преобразования времени в ISO 8601: " + time + ", " + e.getMessage());
            return null;
        }
    }


    private boolean hasUnknownFields(TrainInfo trainInfo) {
        return "Unknown".equals(trainInfo.getTrainNumber()) ||
                "Unknown".equals(trainInfo.getDepartureStation()) ||
                "Unknown".equals(trainInfo.getArrivalStation()) ||
                "Unknown".equals(trainInfo.getBrand()) ||
                "Unknown".equals(trainInfo.getTrainClass()) ||
                "Unknown".equals(trainInfo.getCoachClass()) ||
                "Unknown".equals(trainInfo.getCurrency()) ||
                "Unknown".equals(trainInfo.getFare()) ||
                "Unknown".equals(trainInfo.getSearchSessionId());
    }
}