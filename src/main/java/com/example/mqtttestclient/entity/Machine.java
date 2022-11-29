package com.example.mqtttestclient.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "machine")
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    private Plant plant;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    private List<Device> devices;
}
