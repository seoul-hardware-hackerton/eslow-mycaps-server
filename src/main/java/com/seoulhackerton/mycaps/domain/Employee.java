package com.seoulhackerton.mycaps.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;


@Data
@Entity
@NoArgsConstructor
public class Employee {

    private @Id
    @GeneratedValue
    Long id;
    private String name;
    private String role;

    public Employee(String name, String role) {
        this.name = name;
        this.role = role;
    }
}