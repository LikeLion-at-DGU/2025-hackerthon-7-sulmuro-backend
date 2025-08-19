package com.example.sulmuro_app.domain.market;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String notes;

    private String locationDesc;

    @OneToMany(mappedBy = "store")
    private List<MenuItem> menuItems = new ArrayList<>();
}