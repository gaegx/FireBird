package com.gaegxh.firebirdtask2.model;

import com.gaegxh.firebirdtask2.model.CardInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PaymentRequestDto {

    private String paymentMethod;
    private String phoneNumber;
    private CardInfo card;
    private String nameOnCard;
    private String email;
    private String country;
    private boolean termsAndConditions;
    private Map<String, Object> paidServices;
    private List<List<String>> seatPreference;
    private String returnUrl;
    private int screen_height;
    private int screen_width;
}
