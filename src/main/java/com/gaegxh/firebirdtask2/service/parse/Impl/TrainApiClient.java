package com.gaegxh.firebirdtask2.service.parse.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.ArrayList;
import java.util.List;

public class TrainApiClient {

    private final CookieConfig cookieConfig;

    public TrainApiClient(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    public List<JsonObject> fetchFares(String searchSessionId, String trainKey) {
        List<JsonObject> fares = new ArrayList<>();
        if (searchSessionId == null || trainKey == null) {
            return fares;
        }

        try {
            String url = "https://back.rail.ninja/api/v2/timetable/coach-class";

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("search_session_id", searchSessionId);
            requestBody.addProperty("train_key", trainKey);

            HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .header("Cookie", cookieConfig.getCookie())
                    .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                    .body(requestBody.toString())
                    .asString();

            if (response.getStatus() == 200) {
                JsonArray faresArray = JsonParser.parseString(response.getBody()).getAsJsonArray();

                for (JsonElement element : faresArray) {
                    if (element.isJsonObject()) {
                        fares.add(element.getAsJsonObject());
                    }
                }
            } else {
                System.err.println("Ошибка запроса Coach-Class API: HTTP " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("Ошибка запроса Coach-Class API: " + e.getMessage());
        }

        return fares;
    }
}
