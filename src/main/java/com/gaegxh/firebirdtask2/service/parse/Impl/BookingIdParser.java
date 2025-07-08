package com.gaegxh.firebirdtask2.service.parse.Impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

@Service
public class BookingIdParser {

    public static String parseBookingId(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("booking_id")) {
                return jsonObject.get("booking_id").getAsString();
            }
        }
        return null;
    }
}
