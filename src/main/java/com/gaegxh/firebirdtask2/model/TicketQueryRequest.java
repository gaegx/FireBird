package com.gaegxh.firebirdtask2.model;

import lombok.Getter;

@Getter
public class TicketQueryRequest {
    private final String departureStationUuid;
    private final String arrivalStationUuid;
    private final String departureDate;
    private final SearchCriteria searchCriteria;

    private TicketQueryRequest(Builder builder) {
        this.departureStationUuid = builder.departureStationUuid;
        this.arrivalStationUuid = builder.arrivalStationUuid;
        this.departureDate = builder.departureDate;
        this.searchCriteria = builder.searchCriteria;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String departureStationUuid;
        private String arrivalStationUuid;
        private String departureDate;
        private SearchCriteria searchCriteria;

        public Builder departureStationUuid(String departureStationUuid) {
            this.departureStationUuid = departureStationUuid;
            return this;
        }

        public Builder arrivalStationUuid(String arrivalStationUuid) {
            this.arrivalStationUuid = arrivalStationUuid;
            return this;
        }

        public Builder departureDate(String departureDate) {
            this.departureDate = departureDate;
            return this;
        }

        public Builder searchCriteria(SearchCriteria searchCriteria) {
            this.searchCriteria = searchCriteria;
            return this;
        }

        public TicketQueryRequest build() {
            if (searchCriteria == null) {
                throw new IllegalStateException("SearchCriteria must be provided");
            }
            return new TicketQueryRequest(this);
        }
    }
}