package com.gaegxh.firebirdtask2.service.booking.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.gaegxh.firebirdtask2.mapper.TrainInfoToSearchRequestMapper;
import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.gaegxh.firebirdtask2.service.booking.ExternalBookingService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalBookingServiceImpl implements ExternalBookingService {

    private final TrainInfoToSearchRequestMapper mapper;
    private final CookieConfig cookieConfig;
    private final Gson gson = new Gson();

    @Autowired
    public ExternalBookingServiceImpl(TrainInfoToSearchRequestMapper mapper, CookieConfig cookieConfig) {
        this.mapper = mapper;
        this.cookieConfig = cookieConfig;
    }

    public JsonElement sendBookingRequest(TrainInfo trainInfo) {
        JsonObject requestBody = mapper.toJson(trainInfo).getAsJsonObject();
        requestBody.remove("baseUrl");

        System.out.println("Отправляем JSON:\n" + gson.toJson(requestBody));

        int maxAttempts = 3;
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                attempt++;

                HttpResponse<String> response = Unirest.post("https://back.rail.ninja/api/v1/external-booking")
                        .header("Content-Type", "application/json")
                        .header("Cookie", cookieConfig.getCookie())
                        .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                        .body(gson.toJson(requestBody))
                        .asString();

                if (response.getStatus() == 200 && response.getBody() != null) {
                    return gson.fromJson(response.getBody(), JsonElement.class);
                } else {

                    System.err.println("Попытка " + attempt + ": ошибка API — статус " + response.getStatus());
                    if (attempt == maxAttempts) {
                        throw new RuntimeException("Ошибка API после " + maxAttempts + " попыток. Последний ответ: " + response.getBody());
                    }
                }
            } catch (UnirestException e) {
                System.err.println("Попытка " + attempt + ": ошибка Unirest — " + e.getMessage());
                if (attempt == maxAttempts) {
                    throw new RuntimeException("Ошибка при отправке запроса к API external-booking после " + maxAttempts + " попыток: " + e.getMessage());
                }
            }

            try {
                // Короткая задержка перед повтором
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        throw new RuntimeException("Невозможно выполнить запрос external-booking.");
    }

}
