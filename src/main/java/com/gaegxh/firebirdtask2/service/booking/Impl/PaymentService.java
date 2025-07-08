package com.gaegxh.firebirdtask2.service.booking.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.gaegxh.firebirdtask2.model.PaymentRequestDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CookieConfig cookieConfig;
    private final Gson gson;

    public String sendPayment(String bookingId, PaymentRequestDto paymentRequest) {

        String body = gson.toJson(paymentRequest);
        HttpResponse<String> response = Unirest.put("https://back.rail.ninja/api/v3/internal/train/booking/" + bookingId + "/payment")
                .header("Content-Type", "application/json")
                .header("Cookie", cookieConfig.getCookie())
                .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                .body(body)
                .asString();

        if (response.getStatus() == 200) {
            try {
                JsonObject json = gson.fromJson(response.getBody(), JsonObject.class);

                if (json.has("payment_status")) {
                    return json.get("payment_status").getAsString();
                }

                else if (json.has("status")) {
                    return json.get("status").getAsString();
                }
            } catch (Exception e) {

                return "UNKNOWN";
            }
        }

        return "FAILED";
    }
}
