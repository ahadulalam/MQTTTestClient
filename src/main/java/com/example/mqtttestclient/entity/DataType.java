package com.example.mqtttestclient.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "datatype")
public class DataType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "dataType", cascade = CascadeType.ALL)
    private List<DeviceSensorDataType> deviceSensorDataTypes;

    /*@ManyToMany
    private List<Sensor> sensors;*/

    /*@OneToMany(mappedBy = "dataType", cascade = CascadeType.ALL)
    private List<SensorDataType> sensorDataTypes;

    @OneToMany(mappedBy = "dataType", cascade = CascadeType.ALL)
    private List<Transaction> transactions;*/
}
