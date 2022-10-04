package com.example.mqtttestclient.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "device_sensor_data_type")
@Data
public class DeviceSensorDataType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Sensor sensor;

    @ManyToOne
    private Device device;

    @ManyToOne
    private DataType dataType;

}
