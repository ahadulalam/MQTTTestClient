package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.DeviceWiseParticularSensor;
import com.example.mqtttestclient.entity.ParticularSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceWiseParticularSensorRepository extends JpaRepository<DeviceWiseParticularSensor, Long> {
    Optional<DeviceWiseParticularSensor> findByDeviceIdAndParticularSensorIdAndUniqueSensorId(Long unique_sensor_id, Long device_id, Long particular_sensor_id);
}
