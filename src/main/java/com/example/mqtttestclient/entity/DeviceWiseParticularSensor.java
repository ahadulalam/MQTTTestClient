package com.example.mqtttestclient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceWiseParticularSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long uniqueSensorId; // To inform ECU

    @ManyToOne
    private ParticularSensor particularSensor;

    @ManyToOne
    private Device device;

    @OneToMany(mappedBy = "deviceWiseParticularSensor", cascade = CascadeType.ALL)
    private List<DeviceWiseParticularSensor> deviceWiseParticularSensor;
}
