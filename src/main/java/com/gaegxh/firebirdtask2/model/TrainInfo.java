package com.gaegxh.firebirdtask2.model;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
@Setter
public class TrainInfo {
    String trainNumber;
    String departureTime;
    String arrivalTime;
    String departureStation;
    String departureCity;
    String arrivalStation;
    String arrivalCity;
    String duration;
    String departureStationUuid;
    String arrivalStationUuid;

    String brand;
    String trainClass;
    String coachClass;

    int price;
    String currency;
    String fare;

    String carrierUuid;
    String coachClassUuid;
    String fareUuid;

    String searchSessionId;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TrainInfo trainInfo = new TrainInfo();

        public Builder trainNumber(String trainNumber) {
            trainInfo.trainNumber = trainNumber;
            return this;
        }

        public Builder departureTime(String departureTime) {
            trainInfo.departureTime = departureTime;
            return this;
        }

        public Builder arrivalTime(String arrivalTime) {
            trainInfo.arrivalTime = arrivalTime;
            return this;
        }

        public Builder departureStation(String departureStation) {
            trainInfo.departureStation = departureStation;
            return this;
        }

        public Builder departureCity(String departureCity) {
            trainInfo.departureCity = departureCity;
            return this;
        }

        public Builder arrivalStation(String arrivalStation) {
            trainInfo.arrivalStation = arrivalStation;
            return this;
        }

        public Builder arrivalCity(String arrivalCity) {
            trainInfo.arrivalCity = arrivalCity;
            return this;
        }

        public Builder duration(String duration) {
            trainInfo.duration = duration;
            return this;
        }

        public Builder departureStationUuid(String departureStationUuid) {
            trainInfo.departureStationUuid = departureStationUuid;
            return this;
        }

        public Builder arrivalStationUuid(String arrivalStationUuid) {
            trainInfo.arrivalStationUuid = arrivalStationUuid;
            return this;
        }

        public Builder brand(String brand) {
            trainInfo.brand = brand;
            return this;
        }

        public Builder trainClass(String trainClass) {
            trainInfo.trainClass = trainClass;
            return this;
        }

        public Builder coachClass(String coachClass) {
            trainInfo.coachClass = coachClass;
            return this;
        }

        public Builder price(int price) {
            trainInfo.price = price;
            return this;
        }

        public Builder currency(String currency) {
            trainInfo.currency = currency;
            return this;
        }

        public Builder fare(String fare) {
            trainInfo.fare = fare;
            return this;
        }

        public Builder carrierUuid(String carrierUuid) {
            trainInfo.carrierUuid = carrierUuid;
            return this;
        }

        public Builder coachClassUuid(String coachClassUuid) {
            trainInfo.coachClassUuid = coachClassUuid;
            return this;
        }

        public Builder fareUuid(String fareUuid) {
            trainInfo.fareUuid = fareUuid;
            return this;
        }

        public Builder searchSessionId(String searchSessionId) {
            trainInfo.searchSessionId = searchSessionId;
            return this;
        }

        public TrainInfo build() {
            return trainInfo;
        }
    }

    @Override
    public String toString() {
        String departure = departureStation != null && !departureStation.equals("Unknown")
                ? departureStation + (departureCity != null && !departureCity.equals("Unknown") ? " (" + departureCity + ")" : "")
                : "неизвестно";
        String arrival = arrivalStation != null && !arrivalStation.equals("Unknown")
                ? arrivalStation + (arrivalCity != null && !arrivalCity.equals("Unknown") ? " (" + arrivalCity + ")" : "")
                : "неизвестно";

        return String.format(
                "Поезд %s: %s → %s | Отправление: %s | Прибытие: %s | Длительность: %s | Класс поезда: %s | Класс вагона: %s | Цена: %d %s | Бренд: %s | Тариф: %s | carrierUuid: %s | coachClassUuid: %s | fareUuid: %s | departureStationUuid: %s | arrivalStationUuid: %s | searchSessionId: %s",
                trainNumber != null ? trainNumber : "неизвестно",
                departure,
                arrival,
                departureTime != null ? departureTime : "неизвестно",
                arrivalTime != null ? arrivalTime : "неизвестно",
                duration != null ? duration : "неизвестно",
                trainClass != null ? trainClass : "неизвестно",
                coachClass != null ? coachClass : "неизвестно",
                price,
                currency != null ? currency : "неизвестно",
                brand != null ? brand : "неизвестно",
                (fare != null && !fare.equalsIgnoreCase("Unknown")) ? fare : "без тарифа",
                carrierUuid != null ? carrierUuid : "неизвестно",
                coachClassUuid != null ? coachClassUuid : "неизвестно",
                fareUuid != null ? fareUuid : "неизвестно",
                departureStationUuid != null ? departureStationUuid : "неизвестно",
                arrivalStationUuid != null ? arrivalStationUuid : "неизвестно",
                searchSessionId != null ? searchSessionId : "неизвестно"
        );
    }

    public boolean hasNullOrUnknownFields() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value == null) {
                    return true;
                }
                if (value instanceof String) {
                    String strVal = (String) value;
                    if (strVal.isEmpty() || strVal.equalsIgnoreCase("Unknown")) {
                        return true;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            return true;
        }
        return false;
    }
    public String[] toCsvRow() {
        return new String[]{
                trainNumber != null ? trainNumber : "неизвестно",
                departureStation != null ? departureStation : "неизвестно",
                arrivalStation != null ? arrivalStation : "неизвестно",
                departureTime != null ? departureTime : "неизвестно",
                arrivalTime != null ? arrivalTime : "неизвестно",
                duration != null ? duration : "неизвестно",
                String.valueOf(price),
                trainClass != null ? trainClass : "неизвестно",
                coachClass != null ? coachClass : "неизвестно",
                String.valueOf(price),
                currency != null ? currency : "неизвестно",
                fare != null ? fare : "неизвестно"
        };
    }
}