package com.gaegxh.firebirdtask2.dto;

import lombok.Data;
import java.util.List;

@Data
public class PassengersDto {
    private int adults;
    private int children;
    private List<Integer> childrenAge;
}