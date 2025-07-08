package com.gaegxh.firebirdtask2.service.parse.Impl;

import com.gaegxh.firebirdtask2.model.IntermediateTrainInfo;
import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrainInfoBuilder {

    private final TrainJsonExtractor extractor;
    private final TrainApiClient apiClient;
    private final UniqueTrainInfoTracker uniqueTracker;

    public TrainInfoBuilder(TrainJsonExtractor extractor, TrainApiClient apiClient, UniqueTrainInfoTracker uniqueTracker) {
        this.extractor = extractor;
        this.apiClient = apiClient;
        this.uniqueTracker = uniqueTracker;
    }

    public List<TrainInfo> buildTrainInfos(JsonObject train, String trainKey, String searchSessionId) {
        // Шаг 1: Собираем промежуточные данные
        IntermediateTrainInfo intermediate = collectIntermediateData(train, trainKey, searchSessionId);

        return buildFromIntermediate(intermediate);
    }

    private IntermediateTrainInfo collectIntermediateData(JsonObject train, String trainKey, String searchSessionId) {
        List<JsonObject> fares = new ArrayList<>();
        if (searchSessionId != null && trainKey != null) {
            fares = apiClient.fetchFares(searchSessionId, trainKey);
        }
        return new IntermediateTrainInfo(train, trainKey, searchSessionId, fares);
    }

    private List<TrainInfo> buildFromIntermediate(IntermediateTrainInfo intermediate) {
        List<TrainInfo> result = new ArrayList<>();
        JsonObject train = intermediate.getTrainJson();
        String trainKey = intermediate.getTrainKey();
        String searchSessionId = intermediate.getSearchSessionId();
        List<JsonObject> fares = intermediate.getFares();

        try {
            String trainNumber = extractor.extractString(train, "train_number");
            String departureTimeRaw = extractor.extractString(train, "departure_datetime");
            String arrivalTimeRaw = extractor.extractString(train, "arrival_datetime");

            String departureTime = formatTime(departureTimeRaw);
            String duration = calculateDuration(departureTimeRaw, arrivalTimeRaw);
            String departureStation = extractor.extractNestedName(train, "departure_station", "single_name");
            String departureCity = extractor.extractCity(train, "departure_station");
            String arrivalStation = extractor.extractNestedName(train, "arrival_station", "single_name");
            String arrivalCity = extractor.extractCity(train, "arrival_station");
            String departureStationUuid = extractor.extractUuid(train, "departure_station", "uuid");
            String arrivalStationUuid = extractor.extractUuid(train, "arrival_station", "uuid");

            String departureStationWithCity = formatStationWithCity(departureStation, departureCity);
            String arrivalStationWithCity = formatStationWithCity(arrivalStation, arrivalCity);

            String brand = extractor.extractNestedName(train, "train_brand", "name");
            String trainClass = extractor.extractNestedName(train, "train_class", "name");
            String carrierUuid = extractor.extractUuid(train, "carrier", "uuid");

            Set<String> uniqueEntries = new HashSet<>();

            // Обработка cheapest_coach_class
            JsonObject cheapestCoachClass = extractor.extractJsonObject(train, "cheapest_coach_class");
            if (cheapestCoachClass != null) {
                String coachClass = extractor.extractNestedName(cheapestCoachClass, "coach_class", "name");
                String coachClassUuid = extractor.extractUuid(cheapestCoachClass, "coach_class", "uuid");

                JsonObject fare = extractor.extractJsonObject(cheapestCoachClass, "cheapest_fare");
                if (fare != null) {
                    int price = extractor.extractPrice(fare);
                    String currency = extractor.extractCurrency(fare);
                    String fareName = extractor.extractFareName(fare);
                    String fareUuid = extractor.extractUuid(fare, "fare", "uuid");

                    if (!("Unknown".equals(fareName) && fareUuid == null)) {
                        String uniqueKey = generateUniqueKey(trainNumber, coachClass, price, fareName, fareUuid);
                        if (departureTime != null && uniqueEntries.add(uniqueKey) && !uniqueTracker.contains(uniqueKey)) {
                            TrainInfo info = buildTrainInfo(trainNumber, departureTime, arrivalTimeRaw, duration,
                                    departureStationWithCity, departureCity,
                                    arrivalStationWithCity, arrivalCity,
                                    departureStationUuid, arrivalStationUuid,
                                    brand, trainClass, coachClass, price, currency,
                                    fareName, carrierUuid, coachClassUuid, fareUuid, searchSessionId);
                            result.add(info);
                            uniqueTracker.add(uniqueKey);
                        }
                    }
                }
            }

            // Обработка fares из API
            for (JsonObject fare : fares) {
                String coachClass = extractor.extractNestedName(fare, "coach_class", "name");
                String coachClassUuid = extractor.extractUuid(fare, "coach_class", "uuid");

                JsonObject cheapestFare = extractor.extractJsonObject(fare, "cheapest_fare");
                if (cheapestFare != null) {
                    int price = extractor.extractPrice(cheapestFare);
                    String currency = extractor.extractCurrency(cheapestFare);
                    String fareName = extractor.extractFareName(cheapestFare);
                    String fareUuid = extractor.extractUuid(cheapestFare, "fare", "uuid");

                    String uniqueKey = generateUniqueKey(trainNumber, coachClass, price, fareName, fareUuid);
                    if (departureTime != null && uniqueEntries.add(uniqueKey) && !uniqueTracker.contains(uniqueKey)) {
                        TrainInfo info = buildTrainInfo(trainNumber, departureTime, arrivalTimeRaw, duration,
                                departureStationWithCity, departureCity,
                                arrivalStationWithCity, arrivalCity,
                                departureStationUuid, arrivalStationUuid,
                                brand, trainClass, coachClass, price, currency,
                                fareName, carrierUuid, coachClassUuid, fareUuid, searchSessionId);
                        result.add(info);
                        uniqueTracker.add(uniqueKey);
                    }
                }

                if (fare.has("fares") && fare.get("fares").isJsonArray()) {
                    JsonArray fareArray = fare.getAsJsonArray("fares");
                    for (JsonElement fareElement : fareArray) {
                        if (!fareElement.isJsonObject()) continue;
                        JsonObject fareObj = fareElement.getAsJsonObject();

                        int price = extractor.extractPrice(fareObj);
                        String currency = extractor.extractCurrency(fareObj);
                        String fareName = extractor.extractFareName(fareObj);
                        String fareUuid = extractor.extractUuid(fareObj, "fare", "uuid");

                        String uniqueKey = generateUniqueKey(trainNumber, coachClass, price, fareName, fareUuid);
                        if (departureTime != null && uniqueEntries.add(uniqueKey) && !uniqueTracker.contains(uniqueKey)) {
                            TrainInfo info = buildTrainInfo(trainNumber, departureTime, arrivalTimeRaw, duration,
                                    departureStationWithCity, departureCity,
                                    arrivalStationWithCity, arrivalCity,
                                    departureStationUuid, arrivalStationUuid,
                                    brand, trainClass, coachClass, price, currency,
                                    fareName, carrierUuid, coachClassUuid, fareUuid, searchSessionId);
                            result.add(info);
                            uniqueTracker.add(uniqueKey);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге поезда: " + e.getMessage());
        }
        return result;
    }

    private TrainInfo buildTrainInfo(String trainNumber, String departureTime, String arrivalTime, String duration,
                                     String departureStation, String departureCity,
                                     String arrivalStation, String arrivalCity,
                                     String departureStationUuid, String arrivalStationUuid,
                                     String brand, String trainClass, String coachClass,
                                     int price, String currency, String fareName,
                                     String carrierUuid, String coachClassUuid, String fareUuid,
                                     String searchSessionId) {
        return TrainInfo.builder()
                .trainNumber(trainNumber)
                .departureTime(departureTime)
                .arrivalTime(formatTime(arrivalTime))
                .duration(duration)
                .departureStation(departureStation)
                .departureCity(departureCity)
                .arrivalStation(arrivalStation)
                .arrivalCity(arrivalCity)
                .departureStationUuid(departureStationUuid)
                .arrivalStationUuid(arrivalStationUuid)
                .brand(brand)
                .trainClass(trainClass)
                .coachClass(coachClass)
                .price(price)
                .currency(currency)
                .fare(fareName)
                .carrierUuid(carrierUuid)
                .coachClassUuid(coachClassUuid)
                .fareUuid(fareUuid)
                .searchSessionId(searchSessionId)
                .build();
    }

    private String formatTime(String rawDateTime) {
        if (rawDateTime == null) return null;
        try {
            LocalDateTime ldt = LocalDateTime.parse(rawDateTime, DateTimeFormatter.ISO_DATE_TIME);
            return ldt.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return rawDateTime;
        }
    }

    private String calculateDuration(String dep, String arr) {
        if (dep == null || arr == null) return "Unknown";
        try {
            LocalDateTime depTime = LocalDateTime.parse(dep, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime arrTime = LocalDateTime.parse(arr, DateTimeFormatter.ISO_DATE_TIME);
            Duration dur = Duration.between(depTime, arrTime);
            long hours = dur.toHours();
            long minutes = dur.toMinutes() % 60;
            return String.format("%dh %02dm", hours, minutes);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String formatStationWithCity(String station, String city) {
        if (station == null) station = "Unknown";
        if (city == null) city = "Unknown";
        if (station.equals(city)) return station;
        return station + " (" + city + ")";
    }

    private String generateUniqueKey(String trainNumber, String coachClass, int price, String fareName, String fareUuid) {
        return trainNumber + "|" + coachClass + "|" + price + "|" + fareName + "|" + (fareUuid == null ? "" : fareUuid);
    }
}