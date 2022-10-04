package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
