package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateEmployeeRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("salary")
    private int salary;
    @JsonProperty("age")
    private int age;
    @JsonProperty("title")
    private String title;
}
