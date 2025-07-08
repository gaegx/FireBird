package com.gaegxh.firebirdtask2.model;

import com.google.gson.JsonObject;
import java.util.List;

public class IntermediateTrainInfo {
    private final JsonObject trainJson;
    private final String trainKey;
    private final String searchSessionId;
    private final List<JsonObject> fares;

    public IntermediateTrainInfo(JsonObject trainJson, String trainKey, String searchSessionId, List<JsonObject> fares) {
        this.trainJson = trainJson;
        this.trainKey = trainKey;
        this.searchSessionId = searchSessionId;
        this.fares = fares;
    }

    public JsonObject getTrainJson() {
        return trainJson;
    }

    public String getTrainKey() {
        return trainKey;
    }

    public String getSearchSessionId() {
        return searchSessionId;
    }

    public List<JsonObject> getFares() {
        return fares;
    }
}