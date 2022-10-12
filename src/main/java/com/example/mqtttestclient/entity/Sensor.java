package com.example.mqtttestclient.entity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "sensor")
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    /*@Enumerated(EnumType.STRING)
    private DataType dataType;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<DeviceSensor> deviceSensors;*/

    /*@ManyToMany(mappedBy = "sensors")
    private List<Device> devices;*/

    /*@OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<SensorDataType> sensorDataTypes;*/

    /*@OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<DeviceSensor> deviceSensors;*/

    /*@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "sensor_data_type",
            joinColumns = @JoinColumn(name = "sensor_id"),
            inverseJoinColumns = @JoinColumn(name = "data_type_id")
    )
    private List<DataType> dataTypes;*/

    /*@ManyToMany(mappedBy = "sensors")
    private List<DataType> dataTypes;*/

   /* @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<SensorDataType> sensorDataTypes;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<Transaction> transactions;*/
}
