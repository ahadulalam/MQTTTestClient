package com.example.mqtttestclient.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "device")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}
