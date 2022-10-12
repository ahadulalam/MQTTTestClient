package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.Device;
import com.example.mqtttestclient.entity.ParticularSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticularSensorRepository extends JpaRepository<ParticularSensor, Long> {
    Optional<ParticularSensor> findBySensorIdAndDataTypeId(Long sensor_id, Long data_type_id);
}
