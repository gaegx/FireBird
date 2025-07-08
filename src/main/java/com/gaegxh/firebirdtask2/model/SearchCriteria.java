package com.gaegxh.firebirdtask2.model;


import lombok.Data;

@Data
public class SearchCriteria {
    public enum SearchType {
        SEARCH,
        PURCHASE
    }

    private final SearchType searchType;
    private final String bestTicketCriteria;

    public SearchCriteria(SearchType searchType, String bestTicketCriteria) {
        this.searchType = searchType;
        this.bestTicketCriteria = bestTicketCriteria;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public String getBestTicketCriteria() {
        return bestTicketCriteria;
    }
}
