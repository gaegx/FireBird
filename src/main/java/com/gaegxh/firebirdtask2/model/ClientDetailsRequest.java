package com.gaegxh.firebirdtask2.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientDetailsRequest {
    private String user_email;
    private String confirm_user_email;
}
