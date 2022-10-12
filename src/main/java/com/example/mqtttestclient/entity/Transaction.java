package com.example.mqtttestclient.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String payload;

    /*@ManyToOne
    private Device device;

    @ManyToOne
    private Sensor sensor;

    @ManyToOne
    private DataType dataType;*/

    @ManyToOne
    private DeviceWiseParticularSensor deviceWiseParticularSensor;
}
