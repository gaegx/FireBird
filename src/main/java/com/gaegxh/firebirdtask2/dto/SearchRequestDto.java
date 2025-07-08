package com.gaegxh.firebirdtask2.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.Map;

@Data
public class SearchRequestDto {
    @SerializedName("baseUrl")
    private String baseUrl;
    private Map<String, LegDto> legs;
    private PassengersDto passengers;
    @SerializedName("search_session_id")
    private String searchSessionId;
}