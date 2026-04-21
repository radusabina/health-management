package com.example.healthmanagementbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnalyzeItem {
    private String name;
    private int quantityGrams;
}
