package com.gaegxh.firebirdtask2.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Passenger {
    private String first_name;
    private String last_name;
    private String full_name;
    private String title;
    private String dob;
    private String category;
}
