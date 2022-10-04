package com.example.mqtttestclient.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "device")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToOne
    private Machine machine;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<DeviceSensorDataType> deviceSensorDataTypes;

    /*@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "device_sensor",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "sensor_id")
    )
    private List<Sensor> sensors;*/
    /*@OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<DeviceSensor> deviceSensor;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Transaction> transactions;*/
}
