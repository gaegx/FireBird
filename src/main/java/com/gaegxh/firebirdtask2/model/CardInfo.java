package com.gaegxh.firebirdtask2.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardInfo {

    private String cardNumber;
    private String cardType;
    private String expirationMonth;
    private String expirationYear;
    private String secretCode;
}
