package com.example.sulmuro_app.domain.test;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // Lombok 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok 어노테이션
@Entity
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false)
    private String name;


    public Test(String name) {
        this.name = name;
    }

}
