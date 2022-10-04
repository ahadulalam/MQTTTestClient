package com.example.mqtttestclient.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "plant")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    private Factory factory;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private List<Machine> machines;
}
