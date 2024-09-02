package com.example.demospel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Element {

    String name;
    String type;
    String code;
    String condition;
    String value;
}
